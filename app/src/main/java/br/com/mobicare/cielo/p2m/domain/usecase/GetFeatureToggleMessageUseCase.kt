package br.com.mobicare.cielo.p2m.domain.usecase

import br.com.mobicare.cielo.commons.domain.repository.local.FeatureTogglePreferenceRepository

class GetFeatureToggleMessageUseCase(private val repository: FeatureTogglePreferenceRepository) {
    suspend operator fun invoke(key: String) = repository.getFeatureToggleMessage(key)
}