package br.com.mobicare.cielo.biometricToken.domain

import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSelfieResponse
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.reactivex.Observable
import retrofit2.Response

interface BiometricTokenRepository {
    fun postBiometricSelfie(body: BiometricSelfieRequest): Observable<BiometricSelfieResponse>

    fun postBiometricSelfie(
        username: String,
        body: BiometricSelfieRequest
    ): Observable<BiometricSelfieResponse>

    fun postBiometricRegisterDevice(
        faceIdToken: String,
        body: BiometricRegisterDeviceRequest
    ): Observable<BiometricSuccessResponse>

    fun getStoneAgeToken(): Observable<TokenStoneAgeResponse>

    fun putResetPassword(
        username: String,
        faceIdToken: String,
        body: BiometricResetPasswordRequest
    ): Observable<Response<Void>>
}