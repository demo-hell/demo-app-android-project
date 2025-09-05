package br.com.mobicare.cielo.biometricToken.data.repository

import br.com.mobicare.cielo.biometricToken.data.dataSource.BiometricTokenDataSource
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSelfieResponse
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.mockk.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BiometricTokenRepositoryTest {

    private val dataSource = mockk<BiometricTokenDataSource>()
    private val repository = BiometricTokenRepositoryImpl(dataSource)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `postBiometricSelfie should delegate to dataSource`() {
        val request = BiometricSelfieRequest("imagem", "photo", "jwt")
        val response = BiometricSelfieResponse("token", 500)

        every {
            dataSource.postBiometricSelfie(request)
        } returns Observable.just(response)

        val result = repository.postBiometricSelfie(request)

        assert(result.blockingFirst() == response)
    }

    @Test
    fun `postBiometricSelfie with username should delegate to dataSource`() {
        val username = "testUser"
        val request = BiometricSelfieRequest("imagem", "photo", "jwt")
        val response = BiometricSelfieResponse("token", 500)

        every {
            dataSource.postBiometricSelfie(request, username)
        } returns Observable.just(response)

        val result = repository.postBiometricSelfie(username, request)

        assert(result.blockingFirst() == response)
    }

    @Test
    fun `postBiometricRegisterDevice should delegate to dataSource`() {
        val faceIdToken = "token"
        val request = BiometricRegisterDeviceRequest("fingerprint")
        val response = BiometricSuccessResponse("success")

        every {
            dataSource.postBiometricRegisterDevice(faceIdToken, request)
        } returns Observable.just(response)

        val result = repository.postBiometricRegisterDevice(faceIdToken, request)

        assert(result.blockingFirst() == response)
    }

    @Test
    fun `getStoneAgeToken should delegate to dataSource`() {
        val response = TokenStoneAgeResponse("token")

        every {
            dataSource.getStoneAgeToken()
        } returns Observable.just(response)

        val result = repository.getStoneAgeToken()

        assert(result.blockingFirst() == response)
    }

    @Test
    fun `putResetPassword should delegate to dataSource`() {
        val username = "username"
        val faceIdToken = "token"
        val request = BiometricResetPasswordRequest("123456", "123456")
        val response: Response<Void> = Response.success(null)

        every {
            dataSource.putResetPassword(request, username, faceIdToken)
        } returns Observable.just(response)

        val result = repository.putResetPassword(username, faceIdToken, request)

        assert(result.blockingFirst() == response)
    }


}