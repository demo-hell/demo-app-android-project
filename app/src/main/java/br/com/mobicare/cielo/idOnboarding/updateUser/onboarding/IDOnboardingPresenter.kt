package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class IDOnboardingPresenter(
    private val view: IDOnboardingContract.View,
    private val userPreferences: UserPreferences
) : IDOnboardingContract.Presenter {

    override fun saveUserViewedIDOnboarding() {
        userPreferences.saveUserViewedIDOnboarding()
        view.onStartID()
    }
}