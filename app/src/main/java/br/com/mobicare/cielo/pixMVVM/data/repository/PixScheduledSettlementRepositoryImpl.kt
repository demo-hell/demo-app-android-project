package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduledSettlementRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixScheduledSettlementRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixScheduledSettlementRepository

class PixScheduledSettlementRepositoryImpl(
    private val remoteDataSource: PixScheduledSettlementRemoteDataSource,
) : PixScheduledSettlementRepository {

    override suspend fun create(otpCode: String?, request: PixScheduledSettlementRequest) =
        remoteDataSource.create(otpCode, request)

    override suspend fun update(otpCode: String?, request: PixScheduledSettlementRequest) =
        remoteDataSource.update(otpCode, request)

}