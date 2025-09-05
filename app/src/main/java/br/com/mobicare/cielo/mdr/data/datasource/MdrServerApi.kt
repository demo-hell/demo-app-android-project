package br.com.mobicare.cielo.mdr.data.datasource

import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface MdrServerApi {
    @POST("site-cielo/v1/merchant/offers/{bannerId}/report/{userDecision}/BANNER")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun postContractDecision(
        @Path("bannerId") bannerId: String,
        @Path("userDecision") userDecision: String,
    ): Response<Void>
}
