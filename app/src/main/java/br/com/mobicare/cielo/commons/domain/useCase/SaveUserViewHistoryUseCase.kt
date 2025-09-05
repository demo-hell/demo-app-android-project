package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository

class SaveUserViewHistoryUseCase(private val repository: UserInformationLocalRepository) {
    suspend operator fun invoke(key: String, value: Boolean = true) =
        repository.saveUserViewHistory(key, value)
}