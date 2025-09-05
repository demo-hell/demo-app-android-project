package br.com.mobicare.cielo.login.firstAccess.data.datasource.remote

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirstAccessServerApi {

    @POST("site-cielo/v1/accounts")
    @Headers(value = ["auth: no-required", "accessToken: required"])
    suspend fun registrationAccount(
        @Header("inviteToken") inviteToken: String?,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Header("X-acf-sensor-data") akamaiSensorData: String?,
        @Header("Content-Type") contentType: String = "application/json",
        @Body accountRegistrationPayLoadRequest: FirstAccessRegistrationRequest
    ): Response<FirstAccessResponse>
}