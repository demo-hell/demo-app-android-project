package br.com.mobicare.cielo.commons.domain.repository.remote

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse

interface FeatureTogglePreferenceRemoteRepository {
    suspend fun getFeatureTogglePreference(): CieloDataResult<FeatureToggleResponse>
}