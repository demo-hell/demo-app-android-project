package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixInfringementDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository

class PixInfringementRepositoryImpl(
    private val remoteDataSource: PixInfringementDataSource
) : PixInfringementRepository {

    override suspend fun getInfringement(idEndToEnd: String): CieloDataResult<PixEligibilityInfringementResponse> {
        return remoteDataSource.getInfringement(idEndToEnd)
    }

    override suspend fun postInfringement(request: PixCreateNotifyInfringementRequest): CieloDataResult<PixCreateNotifyInfringementResponse> {
        return remoteDataSource.postInfringement(request)
    }

}