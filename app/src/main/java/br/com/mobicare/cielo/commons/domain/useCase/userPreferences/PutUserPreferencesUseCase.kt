package br.com.mobicare.cielo.commons.domain.useCase.userPreferences

import br.com.mobicare.cielo.commons.domain.repository.local.UserPreferencesLocalRepository

class PutUserPreferencesUseCase(private val repository: UserPreferencesLocalRepository) {
    suspend operator fun invoke(key: String, value: String, isProtected: Boolean) = repository.put(key, value, isProtected)
    suspend operator fun invoke(key: String, value: Set<String>, isProtected: Boolean) = repository.put(key, value, isProtected)
}