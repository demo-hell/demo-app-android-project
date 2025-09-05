package br.com.mobicare.cielo.mdr.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mdr.data.datasource.MdrRemoteDataSource
import br.com.mobicare.cielo.mdr.domain.repository.MdrRepository

class MdrRepositoryImpl(
    private val dataSource: MdrRemoteDataSource,
) : MdrRepository {
    override suspend fun postContractDecision(
        apiId: String,
        bannerId: Int,
        isAccepted: Boolean,
    ): CieloDataResult<Void> = dataSource.postContractDecision(apiId, bannerId, isAccepted)
}
