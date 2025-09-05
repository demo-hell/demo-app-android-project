package br.com.mobicare.cielo.pix.ui.extract.onboarding

interface PixOnboardingExtractContract {
    interface View {
        fun onShowPixExtract()
    }

    interface Presenter {
        fun saveShowPixOnboardingExtract()
    }
}