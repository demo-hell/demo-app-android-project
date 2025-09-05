package br.com.mobicare.cielo.extrato.presentation.ui

import androidx.fragment.app.Fragment
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

/**
 * Created by silvia.miranda on 13/06/2017.
 */

interface ExtratoContract {

    interface View : IAttached {
        fun showFiltro(){}
        fun hideFiltro(){}
        fun showMoreFilters(quickFilter: QuickFilter, isShowPaymentTypes: Boolean){}
        fun attachedFragment(fragment: Fragment)
        fun showErrorData(){}
        fun validaOutrosPeriodos(): Boolean = false
        fun changeColorFilter(isNotApplied: Boolean)
        fun showCancelTutorial() {}
        fun openBottomSheetMoreOptions(enabledOptionSolesp: Boolean, enabledOptionChargeback: Boolean) {}
    }

    interface Presenter {
        fun callGA(label: String, botao: String, tela: String)
        fun loadExtrato(checkedId: Int, dataInicio: String = "", dataFim: String = "")
        fun loadExtratoByDailyDate(date: DataCustom)
        fun loadExtratoByPeriod(period: String)
        fun loadExtratoByInterval(startDate: DataCustom, endDate: DataCustom)
        fun showByToday(filter: QuickFilter)
        fun showByPeriod(filter: QuickFilter)
        fun showMoreFilters()
        fun refresh(filter: QuickFilter)
        fun resetFilter()
        fun openBottomSheetMoreOptions()
    }
}
