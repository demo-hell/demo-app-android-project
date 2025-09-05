package br.com.mobicare.cielo.pix.ui.extract.router

import br.com.mobicare.cielo.commons.presentation.BaseView

interface PixExtractRouterContract {

    interface View : BaseView {
        fun onShowExtract()
        fun onShowOnboarding()
    }

    interface Presenter {
        fun onShowExtract(isHome: Boolean)
    }

}