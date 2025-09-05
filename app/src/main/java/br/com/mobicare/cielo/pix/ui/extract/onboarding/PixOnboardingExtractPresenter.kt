package br.com.mobicare.cielo.pix.ui.extract.onboarding

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class PixOnboardingExtractPresenter(
    private val view: PixOnboardingExtractContract.View,
    private val userPreferences: UserPreferences
) : PixOnboardingExtractContract.Presenter {

    override fun saveShowPixOnboardingExtract() {
        userPreferences.saveShowPixOnboardingExtract()
        view.onShowPixExtract()
    }
}