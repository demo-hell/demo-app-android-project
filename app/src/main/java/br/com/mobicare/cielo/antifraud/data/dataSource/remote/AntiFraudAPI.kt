package br.com.mobicare.cielo.antifraud.data.dataSource.remote

import br.com.mobicare.cielo.antifraud.data.model.response.AntiFraudSessionIDResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface AntiFraudAPI {

    @GET("site-cielo/v1/taponphone/fraud-analysis/session")
    @Headers(value = ["auth: required", "accessToken: required"])
    suspend fun getSessionID(): Response<AntiFraudSessionIDResponse>

}