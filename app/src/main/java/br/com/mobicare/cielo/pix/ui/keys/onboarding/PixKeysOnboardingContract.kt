package br.com.mobicare.cielo.pix.ui.keys.onboarding

interface PixKeysOnboardingContract {

    interface View {
        fun onShowMyPixKeys()
    }

    interface Presenter {
        fun onSaveOnboardingWasViewed()
    }
}