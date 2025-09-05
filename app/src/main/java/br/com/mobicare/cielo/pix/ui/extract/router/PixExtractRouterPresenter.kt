package br.com.mobicare.cielo.pix.ui.extract.router

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class PixExtractRouterPresenter(
    val view: PixExtractRouterContract.View,
    val userPreferences: UserPreferences
) : PixExtractRouterContract.Presenter {

    override fun onShowExtract(isHome: Boolean) {
        view.showLoading()
        if (isHome)
            view.onShowExtract()
        else {
            if (userPreferences.isShowPixOnboardingExtract)
                view.onShowExtract()
            else
                view.onShowOnboarding()
        }

        view.hideLoading()
    }
}