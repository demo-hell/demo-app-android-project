package br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada

import androidx.annotation.ColorRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface VisaoDetalhadaMeusRecebimentosContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun hideRefreshLoading()
        fun hideMoreLoading()
        fun hideHelpButton()
        fun showError(errorMessage: ErrorMessage)
        fun showBottom(valor: String, @ColorRes idColorRes: Int)
        fun showHeader(brand: String, product: String)
        fun showItens(code: Int, itens: ArrayList<Receivable>)
        fun showMoreItems(code: Int, itens: ArrayList<Receivable>, isFinish: Boolean)
        fun showHelpPopup(code: Int, title: String, message: String)
        fun showErrorEmptyResult()

    }

    interface Presenter {
        fun onStart()
        fun onDestroy()
        fun load(page: Int = 1, pageSize: Int = 25, isRefresh: Boolean = false, customQuickFilter: QuickFilter?, item: Item?, code: Int?)
        fun loadMore(item: Item?)
        fun helpButtonClicked(item: Item?)
    }
}