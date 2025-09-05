package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.pix.domain.ClaimsResponse
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse

interface PixKeyRegistrationContract {

    interface View : BaseView {
        fun onShowPortability(response: ValidateKeyResponse)
        fun onShowClaim(response: ValidateKeyResponse)
        fun onShowErrorValidateKey(error: ErrorMessage? = null)
        fun onSuccess(onAction: () -> Unit)
        fun onSuccessRegisterKey()
        fun onErrorRegisterKey(error: ErrorMessage? = null)

        fun onSuccessCreateClaimOwnership(response: ClaimsResponse)
        fun onSuccessCreateClaimPortability(response: ClaimsResponse)
        fun onErrorCreateClaimOwnership(error: ErrorMessage? = null)
        fun onErrorCreateClaimPortability(error: ErrorMessage? = null)

        fun onShowError(onFirstAction: () -> Unit)
    }

    interface Presenter {
        fun getUsername(): String
        fun getDocument(): String
        fun onValidateKey(
            otp: String,
            key: String?,
            type: String,
            code: String?
        )

        fun onRegisterKey(
            otp: String,
            key: String? = null,
            type: String,
            code: String?,
            isStartAnimation: Boolean = true
        )

        fun onCreateClaim(
            otp: String,
            key: String?,
            type: String,
            claimType: String,
            code: String?
        )

        fun onResume()
        fun onPause()
    }
}