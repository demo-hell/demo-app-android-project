package br.com.mobicare.cielo.pix.ui.keys.onboarding

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class PixKeysOnboardingPresenter(
    private val view: PixKeysOnboardingContract.View,
    private val userPreferences: UserPreferences
) : PixKeysOnboardingContract.Presenter {

    override fun onSaveOnboardingWasViewed() {
        userPreferences.saveOnboardingPixKeysWasViewed()
        view.onShowMyPixKeys()
    }

}