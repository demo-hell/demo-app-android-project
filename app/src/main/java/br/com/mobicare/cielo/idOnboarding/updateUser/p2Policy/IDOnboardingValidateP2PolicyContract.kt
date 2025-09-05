package br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy

import br.com.mobicare.cielo.commons.presentation.BaseView

class IDOnboardingValidateP2PolicyContract {
    interface View: BaseView {
        fun onAllowMeDone() {}
        fun showP2Success() {}
        fun onErrorRefreshToken()
    }

    interface Presenter {
        fun retry()
        fun onResume()
        fun onPause()
    }
}