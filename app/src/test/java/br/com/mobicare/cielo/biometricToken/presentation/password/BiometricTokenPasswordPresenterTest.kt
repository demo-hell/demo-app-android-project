package br.com.mobicare.cielo.biometricToken.presentation.password

import br.com.mobicare.cielo.biometricToken.data.repository.BiometricTokenRepositoryImpl
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class BiometricTokenPasswordPresenterTest {

    private lateinit var view: BiometricTokenPasswordContract.View
    private lateinit var repository: BiometricTokenRepositoryImpl
    private lateinit var uiScheduler: Scheduler
    private lateinit var ioScheduler: Scheduler
    private lateinit var presenter: BiometricTokenPasswordPresenter

    @Before
    fun setup() {
        view = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        uiScheduler = Schedulers.trampoline()
        ioScheduler = Schedulers.trampoline()

        presenter = BiometricTokenPasswordPresenter(
            view,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @After
    fun teardown() {
        presenter.onPause()
    }

    @Test
    fun `resetPassword should call changePasswordSuccess when API call succeeds`() {
        // Given
        val userName = "testUser"
        val faceIdToken = "testToken"
        val password = "newPassword"
        every {
            repository.putResetPassword(userName, faceIdToken, any())
        } returns Observable.just(Response.success(null))

        // When
        presenter.resetPassword(userName, faceIdToken, password)

        // Then
        verify { view.changePasswordSuccess() }
        verify(inverse = true) { view.changePasswordError(any()) }
    }

    @Test
    fun `resetPassword should call changePasswordError when API call fails`() {
        // Given
        val userName = "testUser"
        val faceIdToken = "testToken"
        val password = "newPassword"
        val responseBody = EMPTY
            .toResponseBody("application/json".toMediaTypeOrNull())

        every {
            repository.putResetPassword(userName, faceIdToken, any())
        } returns Observable.just(Response.error(500, responseBody))

        // When
        presenter.resetPassword(userName, faceIdToken, password)

        // Then
        verify { view.changePasswordError(any()) }
    }
}