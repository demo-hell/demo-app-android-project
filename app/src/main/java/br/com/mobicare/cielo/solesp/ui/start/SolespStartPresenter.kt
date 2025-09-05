package br.com.mobicare.cielo.solesp.ui.start

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.SOLESP
import br.com.mobicare.cielo.solesp.ui.start.SolespStartContract.Presenter

class SolespStartPresenter(
    private val view: SolespStartContract.View,
    private val featureTogglePreference: FeatureTogglePreference,
) : Presenter {

    override fun getSolespEnabled() {
        if (featureTogglePreference.getFeatureTogle(SOLESP).not())
            view.showSolespDisabled()
    }

}