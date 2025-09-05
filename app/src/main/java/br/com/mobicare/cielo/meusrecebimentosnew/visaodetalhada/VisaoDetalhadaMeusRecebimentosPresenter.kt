package br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.MeusRecebimentosInteractor
import br.com.mobicare.cielo.meusrecebimentosnew.enums.MeusRecebimentosCodigosEnum
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.DetailSummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Pagination
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import io.reactivex.Scheduler

const val PAGE_NUMBER = 1L
const val AMOUNT = 0.0f
const val RECEIVABLES = "SALES_RECEIVABLES_"

class VisaoDetalhadaMeusRecebimentosPresenter(
        private val view: VisaoDetalhadaMeusRecebimentosContract.View,
        private val repository: MeusRecebimentosInteractor,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler) : VisaoDetalhadaMeusRecebimentosContract.Presenter {

    private var code: Int? = null
    private var quickFilter: QuickFilter? = null
    private var pagination: Pagination? = null

    private val disposible:
            CompositeDisposableHandler = CompositeDisposableHandler()

    override fun onStart() {
        disposible.start()
    }

    override fun onDestroy() {
        disposible.destroy()
    }

    override fun load(page: Int, pageSize: Int, isRefresh: Boolean, customQuickFilter: QuickFilter?, item: Item?, code: Int?) {
        this.code = code
        customQuickFilter?.let {
            quickFilter = customQuickFilter
        }

        quickFilter?.let { itFilter ->
            loadWithCustomFilter(itFilter, page, isRefresh, item, code)
        }
    }

    fun loadWithCustomFilter(itFilter: QuickFilter, page: Int, isRefresh: Boolean, item: Item?, code: Int?) {
        item?.let { itItem ->
            itItem.links?.let { itLinks ->
                val self = itLinks.first()

                disposible
                        .compositeDisposable.add(
                                repository.getDetailSummaryView(self.href,
                                        customId = itFilter.customId,
                                        initialDate = itFilter.initialDate,
                                        finalDate = itFilter.finalDate,
                                        paymentTypeCode = itFilter.paymentType,
                                        cardBrandCode = itFilter.cardBrand,
                                        authorizationCode = itFilter.authorizationCode,
                                        nsu = itFilter.nsu?.toInt(),
                                        operationNumber = itFilter.operationNumber,
                                        roNumber = itFilter.roNumber,
                                        initialAmount = itFilter.initialAmount,
                                        finalAmount = itFilter.finalAmount,
                                        saleCode = itFilter.saleCode,
                                        truncatedCardNumber = itFilter.truncatedCardNumber,
                                        terminal = itFilter.terminal?.get(0),
                                        transactionTypeCode = itFilter.transactionTypeCode,
                                        merchantId = itFilter.merchantId,
                                        page = page,
                                        pageSize = 25)
                                        .observeOn(uiScheduler)
                                        .subscribeOn(ioScheduler)
                                        .doOnSubscribe {
                                            if (page == 1 && isRefresh.not())
                                                view.showLoading()
                                        }
                                        .subscribe({
                                            processResponse(it, itItem, isRefresh, code)
                                        }, {
                                            if (page == 1 && isRefresh.not())
                                                view.hideLoading()
                                            view.showError(APIUtils.convertToErro(it))
                                        })

                        )
            }
        }
    }

    override fun loadMore(item: Item?) {
        pagination?.let {
            if (it.pageNumber + 1 <= it.numPages)
                load(page = (it.pageNumber + 1).toInt(), isRefresh = true, customQuickFilter = quickFilter, item = item, code = code )
            else
                view.hideMoreLoading()

        }
    }

    override fun helpButtonClicked(item: Item?) {
        item?.let {
            it.transactionTypeCode?.let { itCode ->
                it.transactionType.let { itTransactionType ->
                    ConfigurationPreference.instance.getConfigurationValue("$RECEIVABLES${itCode}", EMPTY).let { itMessage ->
                        view.showHelpPopup(itCode, itTransactionType, itMessage)
                    }
                }
            }
        }
    }

    private fun processResponse(response: DetailSummaryViewResponse, item: Item?, isRefresh: Boolean, code: Int?) {
        pagination = response.pagination
        view.hideRefreshLoading()
        view.hideLoading()
        response.summary?.totalNetAmount?.let { itTotalAmount ->
            view.showBottom(itTotalAmount.toPtBrWithNegativeRealString(), if (itTotalAmount > AMOUNT) R.color.green else R.color.red)
        }
        pagination?.let { itPagination ->
            item?.transactionTypeCode?.let { itCode ->
                code?.let { itRequestCode ->
                    val code = when (MeusRecebimentosCodigosEnum.valueOf(itRequestCode)) {
                        MeusRecebimentosCodigosEnum.LIBERACAO_SALDO_RETIDO -> itRequestCode
                        MeusRecebimentosCodigosEnum.VALORES_PENDENTES -> itRequestCode + itCode
                        else -> itCode
                    }
                    response.items?.let { itItems ->
                        if (itItems.isNotEmpty()) {
                            itItems.first().let { itFirst ->
                                itFirst.cardBrand?.let { itCardBrand ->
                                    itFirst.transactionType?.let { transIT ->
                                        view.showHeader(itCardBrand, transIT)
                                    }
                                }
                            }

                            if (itPagination.pageNumber == PAGE_NUMBER)
                                view.showItens(code, ArrayList(response.items))
                            else
                                view.showMoreItems(code, ArrayList(response.items), itPagination.lastPage)
                        } else {
                            if (isRefresh.not())
                                view.showErrorEmptyResult()

                            view.hideMoreLoading()
                        }
                    }
                }
            }
        }
    }

}