package br.com.mobicare.cielo.biometricToken.data.repository

import br.com.mobicare.cielo.biometricToken.data.dataSource.BiometricTokenDataSource
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.biometricToken.domain.BiometricTokenRepository
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.reactivex.Observable
import retrofit2.Response

class BiometricTokenRepositoryImpl(private val dataSource: BiometricTokenDataSource) :
    BiometricTokenRepository {

    override fun postBiometricSelfie(body: BiometricSelfieRequest) =
        dataSource.postBiometricSelfie(body)

    override fun postBiometricSelfie(
        username: String,
        body: BiometricSelfieRequest
    ) = dataSource.postBiometricSelfie(body, username)

    override fun postBiometricRegisterDevice(
        faceIdToken: String,
        body: BiometricRegisterDeviceRequest
    ): Observable<BiometricSuccessResponse> =
        dataSource.postBiometricRegisterDevice(faceIdToken, body)

    override fun getStoneAgeToken(): Observable<TokenStoneAgeResponse> = dataSource.getStoneAgeToken()

    override fun putResetPassword(
        username: String,
        faceIdToken: String,
        body: BiometricResetPasswordRequest
    ): Observable<Response<Void>> = dataSource.putResetPassword(body, username, faceIdToken)

}