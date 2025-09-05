package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.CreateShareDataSource
import br.com.mobicare.cielo.openFinance.data.model.request.CreateShareRequest
import br.com.mobicare.cielo.openFinance.domain.model.CreateShare
import br.com.mobicare.cielo.openFinance.domain.repository.CreateShareRemoteRepository

class CreateShareRepositoryImpl(private val dataSource: CreateShareDataSource): CreateShareRemoteRepository{
    override suspend fun createShare(request: CreateShareRequest): CieloDataResult<CreateShare> {
        return dataSource.createShare(request)
    }
}