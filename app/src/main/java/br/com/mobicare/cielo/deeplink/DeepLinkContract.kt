package br.com.mobicare.cielo.deeplink

import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface DeepLinkContract{

    interface View {
        fun getResponseSendEmail(errorMessage: ErrorMessage)
        fun getResponseResendEmail(code: Int)

        fun sendEmailSucess()

        fun modalResendEmail(title: String, msg: String, response: MultichannelUserTokenResponse?)
    }

    interface Interactor {
        fun verificationEmailConfirmation(token: String?, apiCallbackDefault: APICallbackDefault<Unit, String>)
        fun resendEmail(token: String?, apiCallbackDefault: APICallbackDefault<MultichannelUserTokenResponse, String>)
        fun disposable()
    }
}