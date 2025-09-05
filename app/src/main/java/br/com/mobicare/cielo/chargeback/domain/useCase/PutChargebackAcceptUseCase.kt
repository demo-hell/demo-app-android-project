package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackAcceptRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackAcceptResponse
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackAcceptRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class PutChargebackAcceptUseCase(private val repository: ChargebackAcceptRepository) {

    suspend operator fun invoke(
        otpCode: String,
        request: ChargebackAcceptRequest
    ): CieloDataResult<ChargebackAcceptResponse> =
        repository.putChargebackAccept(otpCode, request)

}