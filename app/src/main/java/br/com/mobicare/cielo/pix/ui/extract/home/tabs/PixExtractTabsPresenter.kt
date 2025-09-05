package br.com.mobicare.cielo.pix.ui.extract.home.tabs

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.commons.utils.getDateInThePastMonth
import br.com.mobicare.cielo.pix.api.extract.PixExtractRepositoryContract
import br.com.mobicare.cielo.pix.domain.FilterExtract
import br.com.mobicare.cielo.pix.domain.PixExtractFilterRequest
import br.com.mobicare.cielo.pix.domain.PixExtractItem
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import br.com.mobicare.cielo.pix.domain.ReceiptsTab
import br.com.mobicare.cielo.pix.domain.ReceiptsTab.SCHEDULES
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.SCHEDULED_EXECUTED
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.util.Calendar
import java.util.Date

private const val ONE_MONTH_AGO = 1
private const val INDEX_DEFAULT = -1

class PixExtractTabsPresenter(
    private val view: PixExtractTabsContract.View,
    private val repository: PixExtractRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixExtractTabsContract.Presenter {

    private var disposable = CompositeDisposable()
    private var isLastPage = false
    private var idEndToEndLast: String? = null
    private var allItems: PixExtractResponse? = null
    private var inLoading = false
    private var mFilter: FilterExtract? = null

    private var _alreadyLoaded = false
    val alreadyLoaded get() = _alreadyLoaded

    override fun getExtract(
        isFirstPage: Boolean,
        receiptsTab: ReceiptsTab,
        filter: FilterExtract?,
        isFilter: Boolean
    ) {
        if (inLoading) return
        if (isLastPage) {
            hideLoading(false)
            return
        }

        mFilter = filter

        disposable.add(
            repository.getExtract(
                PixExtractFilterRequest(
                    idEndToEnd = if (isFirstPage) null else idEndToEndLast,
                    startDate = setupDate(
                        receiptsTab,
                        filter?.startDate,
                        Calendar.getInstance().getDateInThePastMonth(ONE_MONTH_AGO)
                    ),
                    endDate = setupDate(receiptsTab, filter?.endDate, Date().format()),
                    receiptsTab = receiptsTab,
                    period = filter?.period,
                    transferType = filter?.transferType,
                    cashFlowType = filter?.cashFlowType
                )
            )
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    inLoading = true
                    showLoading(isFirstPage)
                }
                .subscribeOn(ioScheduler)
                .subscribe({ itPixExtractResponse ->
                    if (isFirstPage && isLastPage) {
                        view.showFooter()
                        isLastPage = false
                    } else {
                        if (isLastPage.not()) {
                            idEndToEndLast =
                                itPixExtractResponse.items?.lastOrNull()?.receipts?.lastOrNull()?.idEndToEnd
                            if (isFirstPage) setupWhenIsFirstPage(
                                itPixExtractResponse,
                                receiptsTab,
                                isFilter
                            )
                            else {
                                var oldItem: PixExtractItem? = null
                                var newItem: PixExtractItem? = null
                                var index = INDEX_DEFAULT
                                var isEquals = false
                                itPixExtractResponse?.items?.forEachIndexed { _index, _newItem ->
                                    allItems?.items?.forEach { _oldItem ->
                                        if (isTheSameMonth(
                                                _newItem,
                                                _oldItem
                                            )
                                        ) {
                                            isEquals = true
                                            oldItem = _oldItem
                                            newItem = _newItem
                                            index = _index
                                        }
                                    }
                                }
                                if (isEquals) {
                                    addReceiptsToSameMonth(newItem, oldItem)
                                    itPixExtractResponse.items?.let {
                                        if (it.size > ONE) {
                                            addReceiptsToDifferentMonth(index, itPixExtractResponse)
                                        }
                                    }
                                } else addReceiptsToDifferentMonth(index, itPixExtractResponse)

                                if (receiptsTab == SCHEDULES) removeUnusableTransactionStatus()
                                view.showExtract(allItems)
                            }
                        }
                    }
                    inLoading = false
                    hideLoading(isFirstPage)
                    _alreadyLoaded = true
                }, {
                    inLoading = false
                    hideLoading(isFirstPage)
                    if (isFirstPage) {
                        view.showError(APIUtils.convertToErro(it), isFirstPage)
                        view.showFooter()
                    }
                })
        )
    }

    private fun removeUnusableTransactionStatus() {
        allItems?.items?.let { itAllItems ->
            itAllItems.forEach { pixExtractItem ->
                pixExtractItem.receipts?.removeAll { itPixExtractReceipt ->
                    itPixExtractReceipt.transactionStatus == SCHEDULED_EXECUTED.name
                }
            }

            itAllItems.removeAll { itPixExtractItem ->
                itPixExtractItem.receipts?.isEmpty() == true
            }
        }
    }

    private fun setupDate(
        receiptsTab: ReceiptsTab,
        dateChosenByUser: String? = null,
        date: String?
    ): String? {
        if (receiptsTab == SCHEDULES) return null

        return if (dateChosenByUser.isNullOrBlank().not()) dateChosenByUser
        else date
    }

    private fun addReceiptsToDifferentMonth(
        index: Int,
        extractResponse: PixExtractResponse
    ) {
        extractResponse.items?.forEachIndexed { _index, pixExtractItem ->
            if (_index != index) {
                allItems?.items?.toMutableList()?.plus(pixExtractItem)?.let { itNewItemMonth ->
                    allItems?.items = itNewItemMonth.toMutableList()
                }
            }
        }
    }

    private fun addReceiptsToSameMonth(
        newItem: PixExtractItem?,
        oldItem: PixExtractItem?
    ) {
        newItem?.receipts?.let { itNewItemReceipts ->
            oldItem?.receipts?.toMutableList()?.plus(itNewItemReceipts)?.let { itUpdatedReceipts ->
                oldItem.receipts = itUpdatedReceipts.toMutableList()
            }
        }
    }

    private fun isTheSameMonth(
        newItem: PixExtractItem,
        oldItem: PixExtractItem
    ) = newItem.yearMonth == oldItem.yearMonth

    private fun hideLoading(isFirstPage: Boolean = true) {
        if (isFirstPage)
            view.hideLoading()
        else
            view.hideLoadingMore()
    }

    private fun showLoading(isFirstPage: Boolean = true) {
        if (isFirstPage)
            view.showLoading()
        else
            view.showLoadingMore()
    }

    private fun setupWhenIsFirstPage(
        pixExtractResponse: PixExtractResponse,
        receiptsTab: ReceiptsTab,
        isFilter: Boolean
    ) {
        allItems = pixExtractResponse
        inLoading = false

        if (receiptsTab == SCHEDULES) removeUnusableTransactionStatus()
        shouldShowContent(isFilter)
    }

    private fun shouldShowContent(isFilter: Boolean) {
        if (isFilter && allItems?.items?.firstOrNull() == null)
            view.showNoDataWithFilter()
        else {
            if (allItems?.items?.firstOrNull() == null) view.showFooter()
            else view.showExtract(extract = allItems, isFilter = isFilter)
        }
    }

    override fun onCreate() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onDestroyView() {
        disposable.clear()
    }

    override fun onDestroy() {
        disposable.dispose()
    }

    override fun showMoreFilters() {
        view.showMoreFilters(mFilter)
    }

}