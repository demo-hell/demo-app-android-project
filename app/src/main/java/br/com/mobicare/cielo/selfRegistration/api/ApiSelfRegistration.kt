package br.com.mobicare.cielo.selfRegistration.api

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationConfirmationData

interface ApiSelfRegistration {

    interface  Request {
        fun showLoading()
        fun hideLoading()
        fun onSelfRegistrationOn(selfRegistrationConfirmationData: SelfRegistrationConfirmationData)
        fun showError(error: ErrorMessage, sender: Response)
        fun showError(message: String, title: String, sender: Response)
    }

    interface Response {
        fun onErrorResume()
    }
}