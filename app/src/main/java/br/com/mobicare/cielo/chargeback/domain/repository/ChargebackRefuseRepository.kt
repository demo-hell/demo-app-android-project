package br.com.mobicare.cielo.chargeback.domain.repository

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackRefuseResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ChargebackRefuseRepository {
    suspend fun putChargebackRefuse(
        otpCode: String,
        request: ChargebackRefuseRequest
    ): CieloDataResult<ChargebackRefuseResponse>
}