package br.com.mobicare.cielo.mfa.validationprevioustoken

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface ValidationPreviousTokenContract {
    interface View {
        fun showIncorrectValues()
        fun showSuccess()
        fun showLoading(isShow: Boolean)
        fun showUserBlocked()
        fun onInvalidRequestError(error: ErrorMessage)
        fun onBusinessError(error: ErrorMessage)
        fun onValueError(error: ErrorMessage)
    }

    interface Presenter {
        fun putCode(code: String)
    }
}