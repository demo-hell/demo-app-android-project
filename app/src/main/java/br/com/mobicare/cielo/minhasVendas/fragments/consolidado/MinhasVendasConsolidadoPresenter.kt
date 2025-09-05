package br.com.mobicare.cielo.minhasVendas.fragments.consolidado

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummarySalesHistory
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository

class MinhasVendasConsolidadoPresenter(
        private val view: MinhasVendasConsolidadoContract.View,
        private val repository: MinhasVendasRepository) : MinhasVendasConsolidadoContract.Presenter {

    private var pageNumber: String? = null
    private var quickFilter: QuickFilter? = null
    private var isByRefreshing: Boolean = false
    private var sales: ArrayList<SaleHistory> = ArrayList()

    override fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean) {
        this.quickFilter = filter
        this.sales.clear()
        this.load(filter, isByRefreshing)
    }

    private fun isCancelSellsEnabled(): Boolean =
            quickFilter?.status?.contains(ExtratoStatusDef.CANCELADA) ?: false

    override fun loadMore() {
        this.sales.last().let {
            this.quickFilter?.let { itFilter ->
                this.view.showLoadingMoreItens()
                this.load(itFilter, true, null)
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
            this.load(it, this.isByRefreshing, this.pageNumber)
        }
    }

    override fun onClearRequests() {
        this.repository.disposable()
    }

    override fun onItemClicked(item: SaleHistory) {
        item.date?.let {
            this.quickFilter?.let { itFilter ->
                this.view.showSalesMoviment(QuickFilter.Builder().apply {
                    from(itFilter)
                    initialDate(it)
                    finalDate(it)
                }.build())
            }
        }
    }

    private fun load(filter: QuickFilter, isByRefreshing: Boolean, pageNumber: String? = null) {
        if (!isByRefreshing) {
            this.view.showLoading()
        }
        val token = UserPreferences.getInstance().token
        val authorization = Utils.authorization()
        this.pageNumber = pageNumber
        this.isByRefreshing = isByRefreshing


        this.repository.getSummarySalesHistory(
                token,
                authorization,
                initialDate = filter.initialDate,
                finalDate = filter.finalDate,
                cardBrands = filter.cardBrand,
                paymentTypes = filter.paymentType,
                callback = defaultCallbackHandler(isByRefreshing, pageNumber))
    }

    private fun defaultCallbackHandler(isByRefreshing: Boolean, pageNumber: String?): APICallbackDefault<ResultSummarySalesHistory, String> {
        return object : APICallbackDefault<ResultSummarySalesHistory, String> {
            override fun onError(error: ErrorMessage) {
                if (error.logout) {
                    //this@MinhasVendasConsolidadoPresenter.view.logout(error)
                } else {
                    this@MinhasVendasConsolidadoPresenter.view.showError(error)
                }
            }

            override fun onSuccess(response: ResultSummarySalesHistory) {
                val itens = ArrayList<SaleHistory>()
                response.items?.let {
                    itens.addAll(it)
                }
                this@MinhasVendasConsolidadoPresenter.sales.addAll(itens)
                if (isByRefreshing) {
                    this@MinhasVendasConsolidadoPresenter.view.hideRefreshLoading()
                } else {
                    this@MinhasVendasConsolidadoPresenter.view.hideLoading()
                }
                if (pageNumber != null) {
                    this@MinhasVendasConsolidadoPresenter.view.showMoreSales(itens)
                } else {
                    if (response.items.isNullOrEmpty()) {
                        this@MinhasVendasConsolidadoPresenter.view.showEmptyResult()
                    } else {
                        this@MinhasVendasConsolidadoPresenter.view.showSummary(response.summary)
                        this@MinhasVendasConsolidadoPresenter.view.showSales(this@MinhasVendasConsolidadoPresenter.sales)
                    }
                }
            }
        }
    }
}