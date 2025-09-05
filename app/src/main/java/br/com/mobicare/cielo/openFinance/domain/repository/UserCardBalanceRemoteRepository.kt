package br.com.mobicare.cielo.openFinance.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse

interface UserCardBalanceRemoteRepository {
    suspend fun getUserCardBalance(cardProxy: String): CieloDataResult<PrepaidBalanceResponse>
}