package br.com.mobicare.cielo.posVirtual.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.posVirtual.domain.repository.PosVirtualAccreditationRepository

class PostPosVirtualCreateOrderUseCase(
    private val repository: PosVirtualAccreditationRepository
) {

    suspend operator fun invoke(
        otpCode: String,
        request: OrdersRequest
    ): CieloDataResult<String> =
        repository.postCreateOrder(otpCode, request)

}