package br.com.mobicare.cielo.minhasVendas.fragments.consolidado

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface MinhasVendasConsolidadoContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun hideRefreshLoading()
        fun showLoadingMoreItens()
        fun logout(msg: ErrorMessage)
        fun showError(error: ErrorMessage)
        fun showEmptyResult()
        fun showSales(sales: ArrayList<SaleHistory>)
        fun showMoreSales(sales: List<SaleHistory>)
        fun showSummary(summary: Summary)
        fun showSalesMoviment(filter: QuickFilter)
    }

    interface Presenter {
        fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean = false)
        fun loadMore()
        fun refresh()
        fun retry()
        fun onClearRequests()
        fun onItemClicked(item: SaleHistory)
    }

}