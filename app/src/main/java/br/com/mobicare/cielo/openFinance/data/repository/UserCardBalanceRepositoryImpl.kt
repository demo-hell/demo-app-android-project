package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.openFinance.data.datasource.UserCardBalanceDataSource
import br.com.mobicare.cielo.openFinance.domain.repository.UserCardBalanceRemoteRepository

class UserCardBalanceRepositoryImpl(
    private val dataSource: UserCardBalanceDataSource
):UserCardBalanceRemoteRepository {
    override suspend fun getUserCardBalance(cardProxy: String):
            CieloDataResult<PrepaidBalanceResponse> {
        return dataSource.getUserCardBalance(cardProxy)
    }

}