package br.com.mobicare.cielo.commons.domain.useCase.userPreferences

import br.com.mobicare.cielo.commons.domain.repository.local.UserPreferencesLocalRepository

class DeleteUserPreferencesUseCase(private val repository: UserPreferencesLocalRepository) {
    suspend operator fun invoke(key: String) = repository.delete(key)
}