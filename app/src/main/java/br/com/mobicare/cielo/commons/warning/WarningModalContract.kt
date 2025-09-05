package br.com.mobicare.cielo.commons.warning

import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal

interface WarningModalContract {

    interface View {
        fun onShowOtherWarning()
    }

    interface Presenter {
        fun onSaveUserInteraction(modal: FeatureToggleModal)
    }
}