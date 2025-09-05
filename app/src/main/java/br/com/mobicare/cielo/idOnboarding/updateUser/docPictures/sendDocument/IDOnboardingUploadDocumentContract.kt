package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDocument

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface IDOnboardingUploadDocumentContract {
    interface View: BaseView {
        fun successSendingDocument() {}
        fun successStoneAgeToken(token: String)
        fun errorStoneAgeToken(error: ErrorMessage? = null)
    }

    interface Presenter {
        fun uploadDocument(frontBase64: String?, backBase64: String?)
        fun retry()
        fun onResume()
        fun onPause()
        fun getStoneAgeToken()
    }
}