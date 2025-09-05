package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackRefuseRequest
import br.com.mobicare.cielo.chargeback.data.model.response.ChargebackRefuseResponse
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRefuseRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class PutChargebackRefuseUseCase(private val repository: ChargebackRefuseRepository) {
    suspend operator fun invoke(
        otpCode: String,
        request: ChargebackRefuseRequest
    ): CieloDataResult<ChargebackRefuseResponse> =
        repository.putChargebackRefuse(otpCode, request)
}