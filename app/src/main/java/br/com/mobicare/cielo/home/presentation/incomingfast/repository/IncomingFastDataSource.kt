package br.com.mobicare.cielo.home.presentation.incomingfast.repository

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference

class IncomingFastDataSource(private val api: CieloAPIServices) {

    fun getEligibleToOffer(token: String) = api.getEligibleToOffer()

    fun isEnabledIncomingFastFT() = FeatureTogglePreference.instance
            .getFeatureTogle(FeatureTogglePreference.RECEBA_RAPIDO_CONTRATACAO)
}