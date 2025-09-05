package br.com.mobicare.cielo.featureToggle

import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleParams


/**
 * Created by mbello on 8/30/17.
 */

interface FeatureToggleContract {

    interface Presenter {
        fun callAPI()
        fun onResume()
        fun onPause()
    }

    interface View {
        fun onFeatureToogleSuccess()
        fun onFeatureToogleError()
    }
}
