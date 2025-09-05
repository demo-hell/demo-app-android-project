package br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada

import android.os.Bundle
import androidx.annotation.ColorRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface VisaoSumarizadaMeusRecebimentosContract {

    interface View {
        fun showLoading()
        fun hideLoading()
        fun hideRefreshLoading()
        fun hideMoreLoading()
        fun configureToolbar(name: String)
        fun showError(errorMessage: ErrorMessage)
        fun showHelpPopup(code: Int, title: String, message: String)
        fun showSubHeader(startDate: String, finalDate: String)
        fun showBottom(value: String, @ColorRes color: Int)
        fun showItems(code: Int, itens: ArrayList<Item>, isAnimationEnabled: Boolean = true, quickFilter: QuickFilter?)
        fun showMoreItems(code: Int, itens: ArrayList<Item>, isFinish: Boolean = false)

        fun enableFilterOnMenu(code: Int)
        fun showFilters(quickFilter: QuickFilter)
        fun disableFilterOnMenu()
    }

    interface Presenter {
        fun onStart()
        fun onDestroy()
        fun load(bundle: Bundle?)
        fun loadSummaryView(page: Int, pageSize: Int = 10, isRefresh: Boolean = false,
                            customQuickFilter: QuickFilter? = null)
        fun loadMore(customQuickFilter: QuickFilter? = null)
        fun helpButtonClicked()
        fun filterReceivables(customQuickFilter: QuickFilter? = null)
        fun applyFilter(quickFilter: QuickFilter)
    }

}