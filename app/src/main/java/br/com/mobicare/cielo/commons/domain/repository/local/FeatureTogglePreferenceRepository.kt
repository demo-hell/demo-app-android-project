package br.com.mobicare.cielo.commons.domain.repository.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import br.com.mobicare.cielo.p2m.domain.model.TaxModel

interface FeatureTogglePreferenceRepository {
    suspend fun getFeatureTogglePreference(key: String, isLocal: Boolean = true): CieloDataResult<Boolean>

    suspend fun getFeatureToggleMessage(key: String): CieloDataResult<TaxModel>

    suspend fun getFeatureToggle(key: String): CieloDataResult<Feature>

    suspend fun saveFeatureTogglePreference(featureToggleResponse: FeatureToggleResponse)
}