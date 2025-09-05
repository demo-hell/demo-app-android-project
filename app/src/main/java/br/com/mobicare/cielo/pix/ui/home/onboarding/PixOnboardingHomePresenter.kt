package br.com.mobicare.cielo.pix.ui.home.onboarding

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class PixOnboardingHomePresenter(
    private val view: PixOnboardingHomeContract.View,
    private val userPreferences: UserPreferences
) : PixOnboardingHomeContract.Presenter {

    override fun saveShowPixOnboardingHome() {
        userPreferences.savePixOnboardingHomeViewed()
        view.onShowPixHome()
    }
}