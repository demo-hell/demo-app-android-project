package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument

import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.idOnboarding.model.IDOnboardingStatusResponse
import br.com.mobicare.cielo.idOnboarding.model.TokenStoneAgeResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

class IDOnboardingDigitalDocumentPresenterTest {

    private lateinit var view: IDOnboardingDigitalDocumentContract.View
    private lateinit var repository: IDOnboardingRepository
    private lateinit var uiScheduler: Scheduler
    private lateinit var ioScheduler: Scheduler
    private lateinit var presenter: IDOnboardingDigitalDocumentPresenter

    @Before
    fun setup() {
        view = mockk(relaxed = true)
        repository = mockk(relaxed = true)
        uiScheduler = Schedulers.trampoline()
        ioScheduler = Schedulers.trampoline()

        presenter = IDOnboardingDigitalDocumentPresenter(
            repository,
            view,
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
    fun `uploadDigitalDocument should call successSendingDigitalDocument when API call succeeds`() {
        // Given
        val documentBase64 = "documentBase64"
        val response = IDOnboardingStatusResponse()

        every {
            repository.uploadDocument(any(), any(), any(), any())
        } returns Observable.just(response)

        // When
        presenter.uploadDigitalDocument(documentBase64)

        // Then
        verify { view.hideLoading(any()) }
        verify { view.successSendingDigitalDocument() }
        verify(inverse = true) { view.showError() }
    }

    @Test
    fun `uploadDigitalDocument should call onSelfieError when API call fails`() {
        // Given
        val documentBase64 = "documentBase64"

        every {
            repository.uploadDocument(any(), any(), any(), any())
        } returns Observable.error(Exception("API call failed"))

        // When
        presenter.uploadDigitalDocument(documentBase64)

        // Then
        verify { view.errorSendingDigitalDocument(any()) }
        verify(inverse = true) { view.successSendingDigitalDocument()}
    }
}