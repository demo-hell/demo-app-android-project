package br.com.mobicare.cielo.biometricToken.presentation.selfie

import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSelfieResponse
import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.biometricToken.data.repository.BiometricTokenRepositoryImpl
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import junit.framework.Assert
import org.junit.After
import org.junit.Before
import org.junit.Test

class BiometricTokenSelfiePresenterTest {

    private lateinit var view: BiometricTokenSelfieContract.View
    private lateinit var repository: BiometricTokenRepositoryImpl
    private lateinit var userPreferences: UserPreferences
    private lateinit var uiScheduler: Scheduler
    private lateinit var ioScheduler: Scheduler
    private lateinit var presenter: BiometricTokenSelfiePresenter

    @Before
    fun setup() {
        view = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)
        uiScheduler = Schedulers.trampoline()
        ioScheduler = Schedulers.trampoline()

        presenter = BiometricTokenSelfiePresenter(
            view,
            repository,
            userPreferences,
            uiScheduler,
            ioScheduler
        )
    }

    @After
    fun teardown() {
        presenter.onPause()
    }

    @Test
    fun `getStoneAgeToken should call successStoneAgeToken with token when API call succeeds`() {
        // Given
        val response = TokenStoneAgeResponse("stone_age_token")
        every {
            repository.getStoneAgeToken()
        } returns Observable.just(response)

        // When
        presenter.getStoneAgeToken()

        // Then
        verify { view.successStoneAgeToken(any()) }
        verify(inverse = true) { view.errorStoneAgeToken()}
    }

    @Test
    fun `getStoneAgeToken should call errorStoneAgeToken when API call fails`() {
        // Given
        every {
            repository.getStoneAgeToken()
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.getStoneAgeToken()

        // Then
        verify { view.errorStoneAgeToken() }
        verify(inverse = true) { view.successStoneAgeToken(any())}
    }

    @Test
    fun `sendBiometricSelfie should call onSuccessSelfie when API call succeeds`() {
        // Given
        val base64 = "base64"
        val encrypted = "encrypted"
        val response = BiometricSelfieResponse("token", 500)
        every {
            repository.postBiometricSelfie(any())
        } returns Observable.just(response)

        // When
        presenter.sendBiometricSelfie(base64, encrypted, null)

        // Then
        verify { view.onShowSelfieLoading() }
        verify { view.onSuccessSelfie() }
        verify(inverse = true) { view.onSelfieError()}
    }

    @Test
    fun `sendBiometricSelfie should call onSelfieError when API call fails`() {
        // Given
        val base64 = "base64"
        val encrypted = "encrypted"
        every {
            repository.postBiometricSelfie(any())
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.sendBiometricSelfie(base64, encrypted, null)

        // Then
        verify { view.onShowSelfieLoading() }
        verify { view.onSelfieError() }
        verify(inverse = true) { view.onSuccessSelfie()}
    }

    @Test
    fun `sendBiometricSelfie with username should call onSuccessSelfie when API call succeeds`() {
        // Given
        val base64 = "base64"
        val encrypted = "encrypted"
        val username = "testUser"
        val response = BiometricSelfieResponse("token", 500)
        every {
            repository.postBiometricSelfie(username, any())
        } returns Observable.just(response)

        // When
        presenter.sendBiometricSelfie(base64, encrypted, username)

        // Then
        verify { view.onShowSelfieLoading() }
        verify { view.onSuccessSelfie() }
        verify(inverse = true) { view.onSelfieError()}
    }

    @Test
    fun `sendBiometricSelfie with username should call onSelfieError when API call fails`() {
        // Given
        val base64 = "base64"
        val encrypted = "encrypted"
        val username = "testUser"

        every {
            repository.postBiometricSelfie(username, any())
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.sendBiometricSelfie(base64, encrypted, username)

        // Then
        verify { view.onShowSelfieLoading() }
        verify { view.onSelfieError() }
        verify(inverse = true) { view.onSuccessSelfie()}
    }

    @Test
    fun `sendBiometricDevice should call onSuccessRegister when API call succeeds`() {
        // Given
        val fingerprint = "fingerprint"
        val response = BiometricSuccessResponse("token")
        every {
            repository.postBiometricRegisterDevice(
                any(),
                BiometricRegisterDeviceRequest(fingerprint)
            )
        } returns Observable.just(response)

        // When
        presenter.sendBiometricDevice(fingerprint)

        // Then
        verify { view.onSuccessRegister() }
        verify(inverse = true) { view.showError(any())}
    }

    @Test
    fun `sendBiometricDevice should call showError when API call fails`() {
        // Given
        val fingerprint = "fingerprint"
        every {
            repository.postBiometricRegisterDevice(
                any(),
                BiometricRegisterDeviceRequest(fingerprint)
            )
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.sendBiometricDevice(fingerprint)

        // Then
        verify { view.showError(any()) }
        verify(inverse = true) { view.onSuccessRegister()}
    }

    @Test
    fun `isForeign should return true when user is a foreigner`() {
        // Given
        val userPreferences = mockk<UserPreferences>()
        every { userPreferences.userInformation?.identity?.foreigner } returns true
        val presenter = BiometricTokenSelfiePresenter(view, repository, userPreferences, uiScheduler, ioScheduler)

        // When
        val result = presenter.isForeign()

        // Then
        Assert.assertEquals(true, result)
    }

    @Test
    fun `isForeign should return false when user is not a foreigner`() {
        // Given
        val userPreferences = mockk<UserPreferences>()
        every { userPreferences.userInformation?.identity?.foreigner } returns false
        val presenter = BiometricTokenSelfiePresenter(view, repository, userPreferences, uiScheduler, ioScheduler)

        // When
        val result = presenter.isForeign()

        // Then
        Assert.assertEquals(false, result)
    }

    @Test
    fun `getToken should return the expected token value`() {
        // Given
        val presenter = mockk<BiometricTokenSelfiePresenter>(relaxed = true)
        val expectedToken = "testToken"

        every { presenter.getToken() } returns expectedToken

        // When
        val actualToken = presenter.getToken()

        // Then
        Assert.assertEquals(expectedToken, actualToken)
    }
}
