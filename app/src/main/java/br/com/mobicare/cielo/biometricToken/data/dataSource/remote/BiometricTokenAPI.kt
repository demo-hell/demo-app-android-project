package br.com.mobicare.cielo.biometricToken.data.dataSource.remote

import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSelfieResponse
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface BiometricTokenAPI {

    @POST("site-cielo/v1/accounts/face-id/token")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun biometricSelfie(
        @Body body: BiometricSelfieRequest
    ): Observable<BiometricSelfieResponse>

    @POST("site-cielo/v1/accounts/face-id/token/{username}")
    @Headers(value = ["auth: no-required", "accessToken: no-required", "ec: no-required"])
    fun biometricSelfie(
        @Path("username") userName: String,
        @Body body: BiometricSelfieRequest
    ): Observable<BiometricSelfieResponse>

    @POST("site-cielo/v1/accounts/device/register")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun biometricRegisterDevice(
        @Header("faceid-token") faceIdToken: String,
        @Body body: BiometricRegisterDeviceRequest
    ): Observable<BiometricSuccessResponse>

    @GET("site-cielo/v1/authorities/document-scan/signed-token")
    fun getStoneAgeToken(): Observable<TokenStoneAgeResponse>

    @PUT("site-cielo/v1/accounts/forgot-password")
    fun putResetPassword(
        @Query("username") userName: String,
        @Header("faceid-token") faceIdToken: String,
        @Body body: BiometricResetPasswordRequest
    ): Observable<Response<Void>>
}