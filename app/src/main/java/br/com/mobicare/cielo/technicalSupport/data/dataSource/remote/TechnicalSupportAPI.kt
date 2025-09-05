package br.com.mobicare.cielo.technicalSupport.data.dataSource.remote

import br.com.mobicare.cielo.technicalSupport.data.model.request.BatteryRequest
import br.com.mobicare.cielo.technicalSupport.data.model.response.BatteryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TechnicalSupportAPI {

    @POST("site-cielo/v1/merchant/supplies/battery")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postChangeBattery(
        @Body body: BatteryRequest
    ): Response<BatteryResponse>

}