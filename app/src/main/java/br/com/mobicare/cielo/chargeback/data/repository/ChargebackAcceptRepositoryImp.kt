package br.com.mobicare.cielo.chargeback.data.repository

import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackAcceptRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackAcceptResponse
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackAcceptRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ChargebackAcceptRepositoryImp(
    private val dataSource: ChargebackAcceptRemoteDataSource
) : ChargebackAcceptRepository {

    override suspend fun putChargebackAccept(
        otpCode: String,
        request: ChargebackAcceptRequest
    ): CieloDataResult<ChargebackAcceptResponse> =
        dataSource.putChargebackAccept(otpCode, request)

}