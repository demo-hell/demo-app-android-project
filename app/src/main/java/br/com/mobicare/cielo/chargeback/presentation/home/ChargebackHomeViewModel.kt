package br.com.mobicare.cielo.chargeback.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackPendingUseCase
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackTreatedUseCase
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.DONE
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.PENDING
import br.com.mobicare.cielo.chargeback.utils.ChargebackConstants.TREATED
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THIRTY_LONG
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChargebackHomeViewModel(
    private val chargebackPendingUseCase: GetChargebackPendingUseCase,
    private val chargebackTreatedUseCase: GetChargebackTreatedUseCase,
    private val user: GetUserObjUseCase
) : ViewModel() {

    private val _pendingChargebackListLiveData = MutableLiveData<UiState<Chargebacks>>()
    val pendingChargebackListLiveData: LiveData<UiState<Chargebacks>> get() = _pendingChargebackListLiveData

    private val _treatedChargebackListLiveData = MutableLiveData<UiState<Chargebacks>>()
    val treatedChargebackListLiveData: LiveData<UiState<Chargebacks>> get() = _treatedChargebackListLiveData

    private var controlPending = false
    private var controlTreated = false

    var isLastPagePending = false
    var isLastPageTreated = false

    var isTreatedChargebacksAlreadyLoaded = false
    var isPendingChargebackAlreadyLoaded = false

    var pagePending = ONE
    var pageTreated = ONE

    var isMoreLoadingPending = false
    var isMoreLoadingTreated = false

    private lateinit var chargebackPendingParams: ChargebackListParams
    private lateinit var chargebackTreatedParams: ChargebackListParams

    var isFiltering = false



    init {
        createDefaultChargebackPendingParams()
        createDefaultChargebackTreatedParams()
    }


    private fun createDefaultChargebackPendingParams() {
        chargebackPendingParams = ChargebackListParams(
            status = PENDING, page = pagePending
        )
    }

    private fun createDefaultChargebackTreatedParams() {
        chargebackTreatedParams = ChargebackListParams(
            status = DONE,
            page = pageTreated,
            initDate = LocalDate.now().minusDays(THIRTY_LONG).toString(),
            finalDate = LocalDate.now().toString()
        )
    }


    fun applyFilter(params: ChargebackListParams) {
        isFiltering = true
        when(params.status){
            PENDING -> {
                resetPendingVMParams()
                chargebackPendingParams = params
                getChargebackPending()

            }
            DONE -> {
                resetTreatedVMParams()
                chargebackTreatedParams = params
                getChargebackTreated()
            }
        }
    }


    fun clearFilters(chargebackStatus: String){
        isFiltering = false
        when(chargebackStatus) {
            PENDING -> {
                resetPendingVMParams()
                createDefaultChargebackPendingParams()
                getChargebackPending()
            }
            DONE -> {
                resetTreatedVMParams()
                createDefaultChargebackTreatedParams()
                getChargebackTreated()
            }
        }
    }


    private fun resetPendingVMParams() {
        controlPending = false
        isLastPagePending = false
        isPendingChargebackAlreadyLoaded = false
        pagePending = ONE
        isMoreLoadingPending = false
    }

    private fun resetTreatedVMParams() {
        controlTreated = false
        isLastPageTreated = false
        isTreatedChargebacksAlreadyLoaded = false
        pageTreated = ONE
        isMoreLoadingTreated = false
    }



    fun getChargebackPending() {
        val pendingFlag = PENDING
        if (isLastPagePending) {
            return
        }
        val pageControl = pagePending <= ONE
        selectShowLoading(
            requisitionControl = true,
            isLoading = pagePending <= ONE,
            flag = pendingFlag,
            _pendingChargebackListLiveData
        )
        viewModelScope.launch {
            isPendingChargebackAlreadyLoaded = false
            chargebackPendingUseCase(chargebackPendingParams)
                .onSuccess { chargebacks ->
                    selectShowLoading(
                        requisitionControl = false,
                        isLoading = pagePending <= ONE,
                        flag = pendingFlag,
                        _pendingChargebackListLiveData
                    )
                    getValues(chargebacks, pendingFlag, _pendingChargebackListLiveData)
                }.onError { dataResultApiError ->
                    _pendingChargebackListLiveData.value = UiState.HideLoading
                    context.let { cont ->
                        newErrorHandler(
                            cont,
                            user,
                            dataResultApiError.apiException.newErrorMessage
                        ) {
                            if (pageControl)
                                _pendingChargebackListLiveData.value = UiState.Error(dataResultApiError.apiException.newErrorMessage)
                        }
                    }
                }.onEmpty {
                    if (pageControl)
                        _pendingChargebackListLiveData.value = UiState.Empty
                }
        }
    }

    fun getChargebackTreated() {
        val treatedFlag = TREATED
        if (isLastPageTreated) {
            return
        }
        val pageControl = pageTreated <= ONE
        selectShowLoading(
            requisitionControl = true,
            isLoading = pageTreated <= ONE,
            flag = treatedFlag,
            _treatedChargebackListLiveData
        )
        viewModelScope.launch {
            isTreatedChargebacksAlreadyLoaded = false
            chargebackTreatedUseCase(chargebackTreatedParams)
                .onSuccess { chargebacks ->
                    if (pageTreated <= ONE)
                        selectShowLoading(
                            requisitionControl = false,
                            isLoading = true,
                            flag = treatedFlag,
                            _treatedChargebackListLiveData
                        )
                    else
                        selectShowLoading(
                            requisitionControl = false,
                            isLoading = false,
                            flag = treatedFlag,
                            _treatedChargebackListLiveData
                        )
                    getValues(chargebacks, treatedFlag, _treatedChargebackListLiveData)
                }.onError { dataResultApiError ->
                    _treatedChargebackListLiveData.value = UiState.HideLoading
                    context.let { cont ->
                        newErrorHandler(
                            cont,
                            user,
                            dataResultApiError.apiException.newErrorMessage
                        ) {
                            if (pageControl)
                                _treatedChargebackListLiveData.value = UiState.Error(dataResultApiError.apiException.newErrorMessage)
                        }
                    }
                }.onEmpty {
                    _treatedChargebackListLiveData.value = UiState.HideLoading
                    if (pageControl)
                        _treatedChargebackListLiveData.value = UiState.Empty
                }
        }
    }

    private fun <T> selectShowLoading(
        requisitionControl: Boolean = false,
        isLoading: Boolean,
        flag: String,
        mutableLiveData: MutableLiveData<UiState<T>>
    ) {
        if (flag == PENDING) {
            controlPending = requisitionControl
        } else {
            controlTreated = requisitionControl
        }

        if (requisitionControl)
            if (isLoading)
                mutableLiveData.value = UiState.Loading
            else
                mutableLiveData.value = UiState.MoreLoading
        else
            if (isLoading)
                mutableLiveData.value = UiState.HideLoading
            else
                mutableLiveData.value = UiState.HideMoreLoading
    }

    private fun getValues(
        chargebacks: Chargebacks?,
        flag: String,
        mutableLiveData: MutableLiveData<UiState<Chargebacks>>
    ) {
        chargebacks?.let {
            if (flag == PENDING) {
                isLastPagePending = it.lastPage
            } else {
                isLastPageTreated = chargebacks.lastPage
            }

            if (chargebacks.firstPage && chargebacks.content.isEmpty()) {
                mutableLiveData.value = if (isFiltering) UiState.FilterEmpty else UiState.Empty
            } else {
                mutableLiveData.value = UiState.Success(chargebacks)
            }
        }
    }
}
