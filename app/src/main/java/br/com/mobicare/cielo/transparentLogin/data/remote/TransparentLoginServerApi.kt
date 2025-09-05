package br.com.mobicare.cielo.transparentLogin.data.remote

import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface TransparentLoginServerApi {
    @POST("appcielo/v1/user-login/token")
    @Headers(value = ["appToken: no-required", "accessToken: no-required"])
    suspend fun login(
        @Body request: LoginRequest,
        @Header("ignoreSessionExpired") ignoreSessionExpired: String,
        @Header("X-acf-sensor-data") akamaiSensorData: String?
    ): Response<LoginResponse>
}