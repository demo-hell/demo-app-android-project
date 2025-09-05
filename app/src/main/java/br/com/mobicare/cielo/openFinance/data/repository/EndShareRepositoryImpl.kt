package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.EndShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.EndShareRequest
import br.com.mobicare.cielo.openFinance.domain.repository.EndShareRemoteRepository

class EndShareRepositoryImpl(private val dataSource: EndShareDataSource) :
    EndShareRemoteRepository {
    override suspend fun endShare(otpCode: String, request: EndShareRequest): CieloDataResult<Any> {
        return dataSource.endShare(otpCode, request)
    }
}