package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount

interface TapOnPhoneTermAndConditionContract {

    interface View {
        fun showLoading()
        fun hideLoading()

        fun showError(error: ErrorMessage? = null)
        fun onShowCallCenter(error: ErrorMessage)
        fun onRequestTapOnPhoneOrderSuccess(order: String)
        fun onShowRequiredDataField(data: UiRequiredDataField)
    }

    interface Presenter {
        fun onDestroy()
        fun onResume()
        fun requestAccreditation(
            account: TapOnPhoneAccount,
            offer: OfferResponse,
            sessionId: String
        )
    }
}