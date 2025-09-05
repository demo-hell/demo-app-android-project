package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository

class GetUserViewHistoryUseCase(private val repository: UserInformationLocalRepository) {
    suspend operator fun invoke(key: String) = repository.getUserViewHistory(key)
}