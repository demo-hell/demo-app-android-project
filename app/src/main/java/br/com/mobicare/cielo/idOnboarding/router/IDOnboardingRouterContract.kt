package br.com.mobicare.cielo.idOnboarding.router

import br.com.mobicare.cielo.commons.presentation.BaseView

interface IDOnboardingRouterContract {
    interface View : BaseView {
        fun showUpdateUserDataDialog()
        fun showUserWithoutRole()
        fun showP2PicturesStart()
        fun goToHome()
        fun showAccessManager()
    }

    interface Presenter {
        fun getIdOnboardingStatus()
        fun retry()
        fun onResume()
        fun onPause()
    }
}