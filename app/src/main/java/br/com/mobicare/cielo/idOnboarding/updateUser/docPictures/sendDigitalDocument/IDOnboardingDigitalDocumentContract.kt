package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface IDOnboardingDigitalDocumentContract {
    interface View : BaseView {
        fun successStoneAgeToken(token: String)
        fun errorStoneAgeToken(error: ErrorMessage? = null)
        fun successSendingDigitalDocument()
        fun errorSendingDigitalDocument(error: ErrorMessage? = null)
    }

    interface Presenter {
        fun getStoneAgeToken()
        fun uploadDigitalDocument(digitalDocumentBase64: String?)
        fun retry()
        fun onResume()
        fun onPause()
    }
}