package br.com.mobicare.cielo.mfa.router.userWithP2

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface MfaTokenConfigurationContract {
    interface View {
        fun showDifferentDevice()
        fun showUserNeedToFinishP2(error: ErrorMessage? = null)

        fun onResendMfaLoading(isLoading: Boolean = true)
        fun onSuccessResendMfa()
        fun onErrorResendMfa(error: ErrorMessage? = null)
        fun onErrorRefreshToken(error: ErrorMessage? = null)

        fun onConfiguringMfaLoading(isLoading: Boolean = true)
        fun onShowSuccessConfiguringMfa()
        fun onErrorConfiguringMfa(error: ErrorMessage? = null)
    }

    interface Presenter {
        fun enrollment(fingerprint: String?)
        fun challenge(fingerprint: String?)
        fun seedEnrollment(fingerprint: String?, isShowLoading: Boolean)
        fun seedChallenge(fingerprint: String?, isShowLoading: Boolean)
        fun resendMfa(fingerprint: String?)
        fun retry()
        fun onResume()
        fun onPause()
    }
}