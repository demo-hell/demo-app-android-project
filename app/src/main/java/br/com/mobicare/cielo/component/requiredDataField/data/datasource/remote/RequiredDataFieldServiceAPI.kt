package br.com.mobicare.cielo.component.requiredDataField.data.datasource.remote

import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RequiredDataFieldServiceAPI {

    @POST("site-cielo/v1/taponphone/pos-virtual/orders")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postUpdateData(
        @Header("otpCode") otpCode: String,
        @Body body: OrdersRequest
    ): Response<OrdersResponse>

}