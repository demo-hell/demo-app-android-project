package br.com.mobicare.cielo.security.presentation.ui

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.newLogin.NewLoginState

interface BottomSheetSecurityContract {

    interface View : IAttached {
        fun initView()
        fun render(state: NewLoginState)
        fun changeLoginButtonState(isEnabled: Boolean)
        fun successAuth(password: ByteArray? = null)
    }

    interface Presenter {
        fun onLoginButtonClicked(
            identification: String,
            username: String,
            password: String?,
            isKeepData: Boolean,
            fingerprint: String?
        )
        fun onPasswordChanged(value: String?)
        fun validatePassword(identification: String)
    }

}