package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.detail

import br.com.mobicare.cielo.commons.presentation.BaseView

interface PixMyLimitsTrustedDestinationsDetailContract {

    interface View : BaseView {
        fun onSuccessDeleteTrustedDestination()
        fun onErrorDeleteTrustedDestination(onGenericError: () -> Unit)
    }

    interface Presenter {
        fun getUsername(): String
        fun deleteTrustedDestination(otp: String?, id: String?)
        fun onResume()
        fun onPause()
    }
}