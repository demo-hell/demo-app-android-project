package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository
import br.com.mobicare.cielo.featureToggle.domain.Feature

class GetFeatureToggleUseCase(private val repository: FeatureTogglePreferenceRepository) {

    suspend operator fun invoke(key: String): CieloDataResult<Feature> {
        return repository.getFeatureToggle(key)
    }
}