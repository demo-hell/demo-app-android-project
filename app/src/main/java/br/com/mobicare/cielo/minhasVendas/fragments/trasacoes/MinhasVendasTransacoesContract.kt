package br.com.mobicare.cielo.minhasVendas.fragments.trasacoes

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface MinhasVendasTransacoesContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun hideRefreshLoading()
        fun showLoadingMoreItens()
        fun logout(msg: ErrorMessage)
        fun showError(error: ErrorMessage)
        fun showSales(sales: ArrayList<Sale>)
        fun showMoreSales(sales: List<Sale>)
        fun showSummary(summary: Summary)
        fun showEmptyResult()
        fun setToolbarText(text: String)
        fun showMoreFilters(filter: QuickFilter?)
        fun changeColorFilter(isFilterNotSelected: Boolean)
    }

    interface Presenter {
        fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean = false)
        fun loadMore()
        fun refresh(filter: QuickFilter? = null)
        fun retry()
        fun onClearRequests()
        fun showMoreFilters()
    }

}