package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import com.acesso.acessobio_android.services.dto.ResultCamera

interface IDOnboardingUploadSelfieContract {
    interface View: BaseView {
        fun successSendingSelfie()
        fun showErrorInvalidSelfie(error: ErrorMessage? = null, retryCallback: (() -> Unit)? = null)
        fun successStoneAgeToken(token: String)
        fun errorStoneAgeToken(error: ErrorMessage? = null)
        fun basicLoading()
        fun hideBasicLoading()
    }

    interface Presenter {
        fun uploadSelfie(result: ResultCamera?)
        fun uploadSelfie(image: String?, requestId: String?)
        fun getStoneAgeToken()
        fun retry()
        fun onResume()
        fun onPause()
    }
}