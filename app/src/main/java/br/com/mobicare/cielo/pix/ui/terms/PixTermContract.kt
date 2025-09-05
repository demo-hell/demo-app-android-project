package br.com.mobicare.cielo.pix.ui.terms

import br.com.mobicare.cielo.commons.presentation.BaseView

interface PixTermContract {

    interface View : BaseView {
        fun successTermPix()
    }

    interface Presenter {
        fun sentTermPix(isPartner: Boolean)
        fun onPause()
        fun onResume()
    }
}