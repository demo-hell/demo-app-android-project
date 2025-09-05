package br.com.mobicare.cielo.home.presentation.meusrecebimentonew

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary

interface MeusRecebimentosHomeContract{

    interface View: BaseView {
        fun showReceivablesInfo(yesterdayReceivables: Summary?, todayReceivables: Summary?, isByRefreshing: Boolean = false)
        fun showError(error: ErrorMessage? = null, isByRefreshing: Boolean = false) {}
        fun unavailableReceivables(isByRefreshing: Boolean = false)
    }

    interface Presenter {
        fun getAllReceivables(initialDate: String? = null, finalDate: String? = null, isByRefreshing: Boolean = false)
        fun onResume()
        fun onPause()
    }
}