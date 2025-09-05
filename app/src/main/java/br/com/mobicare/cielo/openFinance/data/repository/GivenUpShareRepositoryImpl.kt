package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.GivenUpShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.GivenUpShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.GivenUpShare
import br.com.mobicare.cielo.openFinance.domain.repository.GivenUpShareRemoteRepository

class GivenUpShareRepositoryImpl(private val dataSource: GivenUpShareDataSource) :
    GivenUpShareRemoteRepository {
    override suspend fun givenUpShare(givenUpShareRequest: GivenUpShareRequest):
            CieloDataResult<GivenUpShare> {
        return dataSource.givenUpShare(givenUpShareRequest)
    }
}