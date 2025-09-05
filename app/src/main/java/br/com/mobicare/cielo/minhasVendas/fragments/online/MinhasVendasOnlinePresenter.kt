package br.com.mobicare.cielo.minhasVendas.fragments.online

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.minhasVendas.domain.*
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales


//TODO [MYSALES] - Remover posteriormente - migracao mvvm
class MinhasVendasOnlinePresenter(
        private val view: MinhasVendasOnlineContract.View,
        private val repository: MinhasVendasRepository) : MinhasVendasOnlineContract.Presenter {

    private var pageNumber: Long? = null
    private var quickFilter: QuickFilter? = null
    private var isByRefreshing: Boolean = false
    private var sales = ArrayList<Sale>()
    private var canceledSales = ArrayList<CanceledSale>()
    private var isTheEndList = false

    override fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean) {

        if (UserPreferences.getInstance().isConvivenciaUser) {
            quickFilter = filter
            sales.clear()
            canceledSales.clear()
            isTheEndList = false
            load(filter, isByRefreshing)
        } else {
            view.showFullsecError()
        }

    }

    private fun isCancelSellsEnabled(): Boolean =
            quickFilter?.status?.contains(ExtratoStatusDef.CANCELADA) ?: false

    override fun loadMore() {
        this.sales.last().let {
            this.quickFilter?.let { itFilter ->
                this.view.showLoadingMoreItems()
                if (!isCancelSellsEnabled()) {
                    this.load(itFilter, true, it.id?.toLong())
                } else {
                    this.load(itFilter, true, pageNumber?.plus(1))
                }
            }
        }
    }

    override fun loadMoreCanceledSales() {
        if (isTheEndList.not()) {
            quickFilter?.let { itFilter ->
                view.showLoadingMoreItems()
                load(itFilter, true, pageNumber?.plus(ONE))
            }
        }
    }

    override fun refresh() {
        this.quickFilter?.let {
            this.loadInitial(it, true)
        }
    }

    override fun retry() {
        this.quickFilter?.let {
            this.load(it, this.isByRefreshing, null)
        }
    }

    override fun onClearRequests() {
        this.repository.disposable()
    }

    override fun showSaleDetail(sale: Sale) {
        if (isCancelSellsEnabled().not()) {
            view.showSaleDetail(sale)
        }
    }

    override fun onDestroy() {
        this.repository.onDestroy()
    }

    private fun load(filter: QuickFilter, isByRefreshing: Boolean, pageNumber: Long? = null) {
        if (!isByRefreshing) {
            this.view.showLoading()
        }
        val token = UserPreferences.getInstance().token

        val authorization = Utils.authorization()
        this.pageNumber = pageNumber
        this.isByRefreshing = isByRefreshing

        if (isCancelSellsEnabled()) {
            val canceledSaleCallback = canceledSalesCallback(isByRefreshing, pageNumber)
            this.repository.getCanceledSells(
                token,
                SellsCancelParametersRequest(
                    filter.nsu,
                    filter.tid,
                    filter.initialDate!!, filter.finalDate!!, filter.saleGrossAmount,
                    filter.grossAmount,
                    filter.cardBrand,
                    filter.paymentType,
                    filter.authorizationCode
                ),
                canceledSaleCallback,
                pageNumber = pageNumber,
                pageSize = TWENTY_FIVE
            )
        } else {
            val salesCommonCallback = minhasVendasOnlineDefaultCallback(isByRefreshing, pageNumber)
            this.repository.getSummarySalesOnline(
                    token,
                    authorization,
                    initialDate = filter.initialDate,
                    finalDate = filter.finalDate,
                    cardBrand = filter.cardBrand,
                    paymentType = filter.paymentType,
                    terminal = filter.terminal,
                    status = filter.status,
                    cardNumber = filter.cardNumber,
                    nsu = filter.nsu,
                    authorizationCode = filter.authorizationCode,
                    pageSize = TWENTY_FIVE,
                    page = pageNumber,
                    callback = salesCommonCallback)
        }
    }

    private fun minhasVendasOnlineDefaultCallback(isByRefreshing: Boolean,
                                                  pageNumber: Long?):
            APICallbackDefault<ResultSummarySales, String> {
        return object : APICallbackDefault<ResultSummarySales, String> {
            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    view.logout(error)
                } else {
                    view.showError(error, false)
                }
            }

            override fun onSuccess(response: ResultSummarySales) {
                this@MinhasVendasOnlinePresenter.pageNumber = response.pagination?.pageNumber
                sales.addAll(response.items)

                if (isByRefreshing) {
                    view.hideRefreshLoading()
                } else {
                    view.hideLoading()
                }
                if (pageNumber != null) {
                    view.showMoreSales(response.items)
                } else {
                    if (response.items.isNullOrEmpty()) {
                        view.showEmptyResult(isByRefreshing)
                    } else {
                        view.showSummary(response.summary, quickFilter?.status?.first() ?: ExtratoStatusDef.APROVADA)
                        view.showSales(
                                sales,
                                response.summary,
                                quickFilter?.status?.first() ?: ExtratoStatusDef.APROVADA,
                                isNewLoad = pageNumber == null,
                                isByRefreshing)
                    }
                }
            }
        }
    }

    private fun canceledSalesCallback(
        isByRefreshing: Boolean,
        pageNumberFunc: Long?
    ): APICallbackDefault<ResultSummaryCanceledSales, String> {
        return object : APICallbackDefault<ResultSummaryCanceledSales, String> {
            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    view.logout(error)
                } else {
                    view.showError(error, true)
                }
            }

            override fun onSuccess(response: ResultSummaryCanceledSales) {
                pageNumber = response.pagination?.pageNumber
                response.items?.let { canceledSales.addAll(it) }

                if (isByRefreshing) {
                    view.hideRefreshLoading()
                } else {
                    view.hideLoading()
                }
                if (pageNumberFunc != null) {
                    if (response.items.isNullOrEmpty())
                        isTheEndList = true
                    else
                        view.showMoreCanceledSales(response.items!!)
                } else {
                    if (response.items.isNullOrEmpty()) {
                        view.showEmptyCanceledSales()
                    } else {
                        response.summary?.let { summary ->
                            view.showCanceledSales(
                                canceledSales,
                                summary
                            )
                        }
                    }
                }
            }
        }
    }
}