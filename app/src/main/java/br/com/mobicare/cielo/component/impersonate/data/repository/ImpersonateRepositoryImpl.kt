package br.com.mobicare.cielo.component.impersonate.data.repository

import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.domain.datasource.ImpersonateDataSource
import br.com.mobicare.cielo.component.impersonate.domain.repository.ImpersonateRepository

class ImpersonateRepositoryImpl(
    private val remoteDataSource: ImpersonateDataSource
) : ImpersonateRepository {

    override suspend fun postImpersonate(
        ec: String,
        type: String,
        impersonateRequest: ImpersonateRequest
    ) = remoteDataSource.postImpersonate(ec, type, impersonateRequest)

}