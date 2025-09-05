package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.SharedDataConsentsDataSource
import br.com.mobicare.cielo.openFinance.data.model.response.SharedDataConsentsResponse
import br.com.mobicare.cielo.openFinance.domain.repository.SharedDataConsentsRemoteRepository

class SharedDataConsentsRepositoryImpl(private val dataSource: SharedDataConsentsDataSource) :
    SharedDataConsentsRemoteRepository {
    override suspend fun getConsents(
        journey: String,
        page: String,
        pageSize: String?
    ): CieloDataResult<SharedDataConsentsResponse> {
        return dataSource.getConsents(journey, page, pageSize)
    }
}