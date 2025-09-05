package br.com.mobicare.cielo.chargeback.data.repository

import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRefuseRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackRefuseResponse
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRefuseRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ChargebackRefuseRepositoryImp(private val dataSource: ChargebackRefuseRemoteDataSource) :
    ChargebackRefuseRepository {

    override suspend fun putChargebackRefuse(
        otpCode: String,
        request: ChargebackRefuseRequest
    ): CieloDataResult<ChargebackRefuseResponse> =
        dataSource.putChargebackRefuse(otpCode, request)
}