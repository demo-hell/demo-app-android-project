package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.UpdateShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.UpdateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.UpdateShare
import br.com.mobicare.cielo.openFinance.domain.repository.UpdateShareRemoteRepository

class UpdateShareRepositoryImpl(private val dataSource: UpdateShareDataSource) :
    UpdateShareRemoteRepository {
    override suspend fun updateShare(
        shareId: String,
        request: UpdateShareRequest
    ): CieloDataResult<UpdateShare> {
        return dataSource.updateShare(shareId, request)
    }
}