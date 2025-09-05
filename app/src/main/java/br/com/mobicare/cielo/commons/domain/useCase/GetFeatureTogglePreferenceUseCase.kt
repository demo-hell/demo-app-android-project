package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository

class GetFeatureTogglePreferenceUseCase(private val repository: FeatureTogglePreferenceRepository) {

    suspend operator fun invoke(key: String, isLocal: Boolean = true): CieloDataResult<Boolean> {
        return repository.getFeatureTogglePreference(key, isLocal)
    }
}