package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy

import br.com.mobicare.cielo.commons.presentation.BaseView

interface IDOnboardingValidateP1PolicyContract {
    interface View : BaseView {
        fun onPolicyP1Requested() {}
        fun onPolicyP1StatusArrived(validated: Boolean? = null, userRole: String? = null) {}
        fun showErrorUserNotFound(userRole: String?) {}
        fun onLogout() {}
    }

    interface Presenter {
        fun retry()
        fun onResume()
        fun onPause()
    }
}