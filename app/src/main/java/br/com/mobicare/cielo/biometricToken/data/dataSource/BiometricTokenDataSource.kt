package br.com.mobicare.cielo.biometricToken.data.dataSource

import br.com.mobicare.cielo.biometricToken.data.dataSource.remote.BiometricTokenAPI
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import io.reactivex.Observable
import retrofit2.Response

class BiometricTokenDataSource(
    private val api: BiometricTokenAPI
) {

    fun postBiometricSelfie(body: BiometricSelfieRequest) = api.biometricSelfie(body)

    fun postBiometricSelfie(body: BiometricSelfieRequest, username: String) =
        api.biometricSelfie(username, body)

    fun postBiometricRegisterDevice(faceIdToken: String, body: BiometricRegisterDeviceRequest) =
        api.biometricRegisterDevice(faceIdToken, body)

    fun getStoneAgeToken() = api.getStoneAgeToken()

    fun putResetPassword(
        body: BiometricResetPasswordRequest,
        username: String,
        faceIdToken: String
    ): Observable<Response<Void>> = api.putResetPassword(username, faceIdToken, body)
}