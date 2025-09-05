package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding

interface IDOnboardingContract {

    interface View {
        fun onShowHelpCenter()
        fun onStartID()
    }

    interface Presenter {
        fun saveUserViewedIDOnboarding()
    }
}