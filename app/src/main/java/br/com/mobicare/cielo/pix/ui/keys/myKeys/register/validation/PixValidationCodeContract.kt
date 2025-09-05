package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum

interface PixValidationCodeContract {

    interface View : BaseView {
        fun onSuccessSendCode()
        fun onShowErrorClaim(error: ErrorMessage?)
        fun onSuccessClaim(onAction: () -> Unit)
        fun onSuccessRevokeClaim()
    }

    interface Presenter {
        fun getUsername(): String
        fun onSendValidationCode(key: String?, type: PixKeyTypeEnum?, isClaimFlow: Boolean)

        fun onRevokeClaim(
            otp: String,
            claimId: String?,
            code: String?
        )

        fun onResume()
        fun onPause()
    }
}