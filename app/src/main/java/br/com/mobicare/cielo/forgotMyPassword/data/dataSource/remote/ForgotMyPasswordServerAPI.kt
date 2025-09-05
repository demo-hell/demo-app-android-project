package br.com.mobicare.cielo.forgotMyPassword.data.dataSource.remote

import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.data.model.response.ForgotMyPasswordRecoveryPasswordResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ForgotMyPasswordServerAPI {

    @POST("site-cielo/v1/accounts/forgot-password")
    @Headers("ec: no-required")
    suspend fun postRecoveryPassword(
        @Body data: ForgotMyPasswordRecoveryPasswordRequest,
        @Header("X-acf-sensor-data") akamaiSensorData: String?
    ): Response<ForgotMyPasswordRecoveryPasswordResponse>

}