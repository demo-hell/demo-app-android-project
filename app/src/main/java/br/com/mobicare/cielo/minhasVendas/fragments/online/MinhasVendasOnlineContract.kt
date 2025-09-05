package br.com.mobicare.cielo.minhasVendas.fragments.online

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface MinhasVendasOnlineContract {

    interface View : BaseView {
        fun hideRefreshLoading(){}
        fun showLoadingMoreItems(){}

        fun showSales(sales: ArrayList<Sale>, summary: Summary, saleStatus: Int, isNewLoad: Boolean = false, isByRefreshing: Boolean = false)
        fun showMoreSales(sales: List<Sale>){}
        fun showCanceledSales(sales: ArrayList<CanceledSale>, summary: Summary) {}
        fun showMoreCanceledSales(sales: List<CanceledSale>){}
        fun showSummary(summary: Summary, saleStatus: Int){}

        fun showEmptyResult(isByRefreshing: Boolean = false)
        fun showEmptyCanceledSales() {}
        fun showError(error: ErrorMessage? = null, isCanceledSale: Boolean = false) {}
        fun showFullsecError()

        fun showSaleDetail(sale: Sale){}
    }

    interface WithCanceledSellsView : View {
        fun showCanceledSaleDetail(canceledSale: CanceledSale)
    }

    interface Presenter {
        fun loadInitial(filter: QuickFilter, isByRefreshing: Boolean = false)
        fun loadMore()
        fun loadMoreCanceledSales()
        fun refresh()
        fun retry()
        fun onClearRequests()
        fun onDestroy()
        fun showSaleDetail(sale: Sale)
    }
}