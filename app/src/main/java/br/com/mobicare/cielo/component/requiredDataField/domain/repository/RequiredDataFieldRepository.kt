package br.com.mobicare.cielo.component.requiredDataField.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse

interface RequiredDataFieldRepository {

    suspend fun postUpdateData(
        otpCode: String,
        data: OrdersRequest
    ): CieloDataResult<OrdersResponse>

}