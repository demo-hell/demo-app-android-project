package br.com.mobicare.cielo.biometricToken.presentation.selfie

import br.com.mobicare.cielo.commons.presentation.BaseView

class BiometricTokenSelfieContract {
    interface View : BaseView {
        fun onShowSelfieLoading()
        fun hideAnimatedLoading()
        fun onSuccessSelfie()
        fun onSelfieError()
        fun onSuccessRegister()
        fun successStoneAgeToken(token: String)
        fun errorStoneAgeToken()
    }

    interface Presenter {
        fun sendBiometricSelfie(base64: String?, encrypted: String?, username: String?)
        fun sendBiometricDevice(fingerprint: String)
        fun getStoneAgeToken()
        fun onResume()
        fun onPause()
    }
}