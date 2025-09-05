package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.domain.repository.local.ConfigurationLocalRepository


class GetConfigurationUseCase(private val repository: ConfigurationLocalRepository) {
    suspend operator fun invoke(key: String, default: String) = repository.getConfiguration(key, default)
}