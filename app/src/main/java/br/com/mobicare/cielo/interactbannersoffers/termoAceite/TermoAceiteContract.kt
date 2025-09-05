package br.com.mobicare.cielo.interactbannersoffers.termoAceite

import br.com.mobicare.cielo.commons.presentation.BaseView

interface TermoAceiteContract {
    interface Presenter {
        fun onStop()
        fun onResume()
        fun submitTermoAceite(bannerId: Int)
    }

    interface View: BaseView {
        fun showSuccess()
    }
}