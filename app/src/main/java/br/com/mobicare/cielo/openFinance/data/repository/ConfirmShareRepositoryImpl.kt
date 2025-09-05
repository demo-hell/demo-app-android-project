package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ConfirmShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.ConfirmShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.ConfirmShare
import br.com.mobicare.cielo.openFinance.domain.repository.ConfirmShareRemoteRepository

class ConfirmShareRepositoryImpl(private val dataSource: ConfirmShareDataSource) :
    ConfirmShareRemoteRepository {
    override suspend fun confirmShare(confirmShareRequest: ConfirmShareRequest):
            CieloDataResult<ConfirmShare> {
        return dataSource.confirmShare(confirmShareRequest)
    }
}