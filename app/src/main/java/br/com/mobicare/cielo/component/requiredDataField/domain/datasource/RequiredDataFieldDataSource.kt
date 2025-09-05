package br.com.mobicare.cielo.component.requiredDataField.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse

interface RequiredDataFieldDataSource {

    suspend fun postUpdateData(
        otpCode: String,
        data: OrdersRequest
    ): CieloDataResult<OrdersResponse>

}