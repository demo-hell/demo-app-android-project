package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixExtractUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixReceiptsScheduledUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.AccountEntriesFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PeriodFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.StatusFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.TransactionFilterTypeEnum
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractFilterModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.PixExtractFilterRequestGenerate
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.EmptyTransactions
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.EmptyTransactionsWithActiveFilter
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.Error
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.HideLoading
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.HideLoadingMoreTransactions
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.HideLoadingSwipe
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.ShowLoading
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.ShowLoadingMoreTransactions
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.utils.UIPixExtractPageState.Success
import kotlinx.coroutines.launch
import java.util.Calendar

class PixExtractPageViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getPixExtractUseCase: GetPixExtractUseCase,
    private val getPixReceiptsScheduledUseCase: GetPixReceiptsScheduledUseCase,
) : ViewModel() {
    private val _extractState = MutableLiveData<UIPixExtractPageState>()
    val extractState: LiveData<UIPixExtractPageState> = _extractState

    private val _transactions = emptyList<Any>().toCollection(ArrayList())
    val transactions get() = _transactions

    private val _filterData = MutableLiveData<PixExtractFilterModel>()
    val filterData get() = _filterData

    private var tab: PixReceiptsTab = PixReceiptsTab.TRANSFER

    private var endList = false
    private var idEndToEndLastTransaction = EMPTY
    private var hasStarted = false
    private var lastSchedulingIdentifierCode: String? = null
    private var lastNextDateTimeScheduled: Calendar? = null

    private val filterIsActive
        get(): Boolean {
            val filter = filterData.value

            return filter?.let {
                it.periodType.isSelected || it.accountEntriesType.isSelected || it.statusType.isSelected || it.transactionType.isSelected
            } ?: false
        }

    init {
        _filterData.value = PixExtractFilterModel()
    }

    fun setTab(value: PixReceiptsTab) {
        tab = value
    }

    fun loadTransactions(
        isOnResume: Boolean,
        isSwipe: Boolean,
    ) {
        if (_extractState.value == ShowLoading ||
            _extractState.value == ShowLoadingMoreTransactions ||
            (isOnResume && (endList || _transactions.isNotEmpty()))
        ) {
            return
        }

        if (isSwipe.not()) _extractState.value = ShowLoading

        _transactions.clear()
        endList = false
        idEndToEndLastTransaction = EMPTY
        lastSchedulingIdentifierCode = null
        lastNextDateTimeScheduled = null

        viewModelScope.launch {
            getExtract(isLookingForMoreTransactions = false, isSwipe)
        }
    }

    fun loadMoreTransactions() {
        if (hasStarted &&
            _extractState.value != ShowLoading &&
            _extractState.value != ShowLoadingMoreTransactions &&
            endList.not()
        ) {
            _extractState.value = ShowLoadingMoreTransactions
            viewModelScope.launch {
                getExtract(isLookingForMoreTransactions = true, isSwipe = false)
            }
        }
    }

    fun <T> setFilter(value: T) {
        val currentFilter = _filterData.value

        when (value) {
            is AccountEntriesFilterTypeEnum -> currentFilter?.accountEntriesType = value
            is PeriodFilterTypeEnum -> currentFilter?.periodType = value
            is StatusFilterTypeEnum -> currentFilter?.statusType = value
            is TransactionFilterTypeEnum -> currentFilter?.transactionType = value
        }

        currentFilter?.let {
            _filterData.value = it
            loadTransactions(isOnResume = false, isSwipe = false)
        }
    }

    fun clearFilter() {
        _filterData.value = PixExtractFilterModel()
        loadTransactions(isOnResume = false, isSwipe = false)
    }

    private suspend fun getExtract(
        isLookingForMoreTransactions: Boolean,
        isSwipe: Boolean,
    ) {
        if (tab == PixReceiptsTab.NEW_SCHEDULES) {
            getPixReceiptsScheduledUseCase
                .invoke(
                    GetPixReceiptsScheduledUseCase.Params(
                        lastNextDateTimeScheduled = lastNextDateTimeScheduled?.toString(SIMPLE_DATE_INTERNATIONAL),
                        lastSchedulingIdentifierCode = lastSchedulingIdentifierCode,
                    ),
                )
        } else {
            getPixExtractUseCase
                .invoke(
                    PixExtractFilterRequestGenerate.generate(
                        idEndToEndLastTransaction,
                        tab,
                        _filterData.value,
                    ),
                )
        }.onSuccess {
            setHasStarted()
            processExtract(it, isLookingForMoreTransactions, isSwipe)
        }.onEmpty {
            setHasStarted()
            onEmptyGetExtract(isLookingForMoreTransactions, isSwipe)
        }.onError {
            setHasStarted()
            handleError(it.apiException.newErrorMessage, isLookingForMoreTransactions, isSwipe)
        }
    }

    private fun setHasStarted() {
        hasStarted = true
    }

    private fun setHideLoading(
        isLookingForMoreTransactions: Boolean,
        isSwipe: Boolean,
    ) {
        _extractState.value =
            when {
                isLookingForMoreTransactions -> HideLoadingMoreTransactions
                isSwipe -> HideLoadingSwipe
                else -> HideLoading
            }
    }

    private fun processExtract(
        extract: Any,
        isLookingForMoreTransactions: Boolean,
        isSwipe: Boolean,
    ) {
        val receipts = getReceipts(extract)

        setHideLoading(isLookingForMoreTransactions, isSwipe)

        if (receipts.isNotEmpty()) {
            _transactions.addAll(receipts)
            _extractState.value = Success(ArrayList(receipts), isLookingForMoreTransactions)
        } else {
            endList = true

            if (isLookingForMoreTransactions.not()) {
                _extractState.value =
                    if (filterIsActive) EmptyTransactionsWithActiveFilter else EmptyTransactions
            }
        }
    }

    private fun getReceipts(extract: Any): List<Any> =
        when (extract) {
            is PixExtract -> {
                val list = extract.items.flatMap { it.receipts }

                if (list.isNotEmpty()) {
                    idEndToEndLastTransaction = list.last().idEndToEnd.orEmpty()
                }

                list
            }

            is PixReceiptsScheduled -> {
                lastNextDateTimeScheduled = extract.lastNextDateTimeScheduled
                lastSchedulingIdentifierCode = extract.lastSchedulingIdentifierCode
                endList = extract.last == true

                extract.items?.flatMap { it.receipts.orEmpty() }.orEmpty()
            }

            else -> emptyList()
        }

    private fun onEmptyGetExtract(
        isLookingForMoreTransactions: Boolean,
        isSwipe: Boolean,
    ) {
        setHideLoading(isLookingForMoreTransactions, isSwipe)
        if (isLookingForMoreTransactions.not()) _extractState.value = Error()
    }

    private suspend fun handleError(
        error: NewErrorMessage,
        isLookingForMoreTransactions: Boolean,
        isSwipe: Boolean,
    ) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                setHideLoading(isLookingForMoreTransactions, isSwipe)
            },
            onErrorAction = {
                setHideLoading(isLookingForMoreTransactions, isSwipe)
                _extractState.value = Error(error)
            },
        )
    }
}
