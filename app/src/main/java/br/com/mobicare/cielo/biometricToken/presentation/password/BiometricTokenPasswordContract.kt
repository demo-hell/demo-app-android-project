package br.com.mobicare.cielo.biometricToken.presentation.password

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

class BiometricTokenPasswordContract {
    interface View : BaseView {
        fun changePasswordSuccess()
        fun changePasswordError(error: ErrorMessage? = null)
    }

    interface Presenter {
        fun resetPassword(userName: String, faceIdToken: String, password: String)
        fun onResume()
        fun onPause()
    }
}