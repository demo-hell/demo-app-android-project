package br.com.mobicare.cielo.commons.data.clients.remote

import br.com.mobicare.cielo.me.MeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface UserInformationServerApi {

    @GET("site-cielo/v1/me")
    @Headers("auth: required", "accessToken: required", "token: required")
    suspend fun getUserInformation(): Response<MeResponse>

}