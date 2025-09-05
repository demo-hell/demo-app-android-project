package br.com.mobicare.cielo.idOnboarding.updateUser.userInfo

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView

interface IDOnboardingUpdateUserContract {
    interface View: BaseView {
        val canEdit: Boolean
            get() = true
        val isEmptyValue: Boolean
            get() = false
        val isReviewEditing: Boolean
            get() = false
        val isAnUpdate: Boolean
            get() = false

        fun isValid(value: String?): Boolean = true
        fun updateView() {}
        fun setupListeners() {}
        fun editValueOnclick() {}

        fun showErrorGeneric(error: ErrorMessage? = null) { showError(error) }
        fun showErrorLabel(error: ErrorMessage? = null) { showError(error) }

        fun successValidatingCpfName() {}
        fun showErrorInvalidCPF() { showError(null) }
        fun showErrorIrregularCPF() { showError(null) }
        fun showErrorBlockedIrregularCPF() { showError(null) }
        fun showCpfAlreadyExists() { showError(null) }
        fun showErrorNameMaxTries() { showError(null) }

        fun successSendingEmailCode() {}
        fun successValidatingEmailCode() {}
        fun successExecuteP1() {}
        fun showErrorEmailUnavailable() { showError(null) }
        fun showErrorEmailMaxTries() { showError(null) }
        fun showErrorEmailDomainRestricted(emailDomain: String) {}

        fun successSendingPhoneCode() {}
        fun successValidatingPhoneCode() {}
        fun successShowButtons(isSmsEnabled: Boolean, isWhatsAppEnabled: Boolean) {}
        fun showErrorPhoneUnavailable() { showError(null) }
        fun showErrorPhoneMaxTries() { showError(null) }
        fun showErrorTryAgain() { showError(null) }

        fun p1PolicyRequested() {}
        fun successPolicyP1() {}
    }

    interface Presenter {
        fun retry()
        fun onResume()
        fun onPause()
    }
}