package br.com.mobicare.cielo.superlink.data.datasource.remote

import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SuperLinkServiceApi {

    @GET("site-cielo/v1/ecommerce/payment/link")
    @Headers(value = ["accessToken: required"])
    suspend fun paymentLinkActive(
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Response<PaymentLinkResponse>

}