package br.com.mobicare.cielo.accessManager.domain.usecase

import br.com.mobicare.cielo.accessManager.domain.repository.NewAccessManagerRepository

class GetCustomActiveProfilesUseCase(
    private val repository: NewAccessManagerRepository
) {
    suspend operator fun invoke(
        profileType: String,
        status: String
    ) = repository.getCustomActiveProfiles(profileType, status)
}