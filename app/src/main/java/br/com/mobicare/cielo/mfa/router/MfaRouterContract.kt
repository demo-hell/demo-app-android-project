package br.com.mobicare.cielo.mfa.router

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType

interface MfaRouterContract {

    interface View {
        fun showLoading(isShow: Boolean = true) {}
        fun showError(error: ErrorMessage) {}
        fun showTokenGenerator() {}
        fun showOnboarding() {}
        fun showNotEligible() {}
        fun showMFAStatusPending() {}
        fun showMFAStatusErrorPennyDrop() {}
        fun showMerchantOnboard(status: String?) {}
        fun callPutValuesValidate() {}
        fun callBlockedForAttempt() {}
        fun callTokenReconfiguration() {}
        fun isMfaEligible(isEligible: Boolean) {}

        fun showDifferentDevice() {}
        fun showUserNeedToFinishP2(error: ErrorMessage? = null) {}
        fun showUserWithP2(type: EnrollmentType) {}
        fun bottomSheetConfiguringMfaDismiss() {}
        fun onShowSuccessConfiguringMfa(isShowMessage: Boolean = false) {}
        fun onErrorConfiguringMfa(error: ErrorMessage? = null) {}

        fun onErrorResendPennyDrop(error: ErrorMessage? = null) {}
    }

    interface Presenter {
        fun load(isEnrollment: Boolean = true)
        fun checkIsMfaEligible()
        fun resendPennyDrop(isShowLoading: Boolean = true)
        fun onResume()
        fun onPause()
    }
}