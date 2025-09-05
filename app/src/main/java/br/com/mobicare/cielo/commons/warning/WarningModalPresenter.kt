package br.com.mobicare.cielo.commons.warning

import br.com.mobicare.cielo.commons.domains.entities.PriorityWarningVisualization
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference

class WarningModalPresenter(private val featureTogglePreference: FeatureTogglePreference,
                            private val menuPreference: MenuPreference) : WarningModalContract.Presenter {

    override fun onSaveUserInteraction(modal: FeatureToggleModal) {
        val listWarning = featureTogglePreference.getSawWarning() ?: ArrayList()
        val priorityWarning = PriorityWarningVisualization(idModal = modal.id,
                ec = menuPreference.getEstablishment()?.ec
        )
        listWarning.add(priorityWarning)
        featureTogglePreference.saveSawWarning(listWarning)
    }
}