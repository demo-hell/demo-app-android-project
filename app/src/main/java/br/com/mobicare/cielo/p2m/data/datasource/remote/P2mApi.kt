package br.com.mobicare.cielo.p2m.data.datasource.remote

import retrofit2.Response
import retrofit2.http.*

interface P2mApi {


    @POST("site-cielo/v1/merchant/offers/{bannerId}/report/accept/BANNER")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun putP2mAccept(
        @Path("bannerId") bannerId: String
    ): Response<Void>

}