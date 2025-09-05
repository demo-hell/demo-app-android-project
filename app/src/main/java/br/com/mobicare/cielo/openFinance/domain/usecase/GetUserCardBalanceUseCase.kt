package br.com.mobicare.cielo.openFinance.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.domain.repository.PixMerchantRemoteRepository
import br.com.mobicare.cielo.openFinance.domain.repository.UserCardBalanceRemoteRepository

class GetUserCardBalanceUseCase(private val repository: UserCardBalanceRemoteRepository) {
    suspend operator fun invoke(cardProxy: String): CieloDataResult<PrepaidBalanceResponse> {
        return repository.getUserCardBalance(cardProxy)
    }
}