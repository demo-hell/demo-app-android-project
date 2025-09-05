package br.com.mobicare.cielo.commons.ui.help

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.recebaMais.domain.HelpCenter

interface HelpMainContract {
    interface Presenter : BasePresenter<View> {
        fun loadHelps(idHelp : String)
    }

    interface View : BaseView, IAttached {
        fun helpsSuccess(helpCenter: HelpCenter)

    }

}