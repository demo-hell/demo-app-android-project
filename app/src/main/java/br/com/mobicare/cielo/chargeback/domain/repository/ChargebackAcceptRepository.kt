package br.com.mobicare.cielo.chargeback.domain.repository

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackAcceptResponse
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ChargebackAcceptRepository {
    suspend fun putChargebackAccept(
        otpCode: String,
        request: ChargebackAcceptRequest
    ): CieloDataResult<ChargebackAcceptResponse>
}