package br.com.mobicare.cielo.component.requiredDataField.domain.useCase

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.domain.repository.RequiredDataFieldRepository

class PostUpdateDataRequiredDataFieldUseCase(
    private val repository: RequiredDataFieldRepository
) {

    suspend operator fun invoke(
        otpCode: String,
        data: OrdersRequest
    ) = repository.postUpdateData(otpCode, data)

}