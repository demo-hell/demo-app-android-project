package br.com.mobicare.cielo.solesp.ui.start

import br.com.mobicare.cielo.commons.presentation.BaseView

interface SolespStartContract {

    interface View : BaseView {
        fun showSolespDisabled()
    }

    interface Presenter {
        fun getSolespEnabled()
    }

}