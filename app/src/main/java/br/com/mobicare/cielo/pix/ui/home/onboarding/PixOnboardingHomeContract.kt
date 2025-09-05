package br.com.mobicare.cielo.pix.ui.home.onboarding

interface PixOnboardingHomeContract {
    interface View {
        fun onShowPixHome()
    }

    interface Presenter {
        fun saveShowPixOnboardingHome()
    }
}