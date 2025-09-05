package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.DetainerDataSource
import br.com.mobicare.cielo.openFinance.data.model.response.DetainerResponse
import br.com.mobicare.cielo.openFinance.domain.repository.DetainerRemoteRepository

class DetainerRemoteRepositoryImpl(
    private val dataSource:DetainerDataSource
):DetainerRemoteRepository {
    override suspend fun getDetainer(consentId: String):
            CieloDataResult<DetainerResponse> {
        return dataSource.getDetainer(consentId)
    }
}