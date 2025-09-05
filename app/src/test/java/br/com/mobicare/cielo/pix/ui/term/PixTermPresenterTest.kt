package br.com.mobicare.cielo.pix.ui.term

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.onboarding.PixRepositoryContract
import br.com.mobicare.cielo.pix.ui.terms.PixTermContract
import br.com.mobicare.cielo.pix.ui.terms.PixTermPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response

class PixTermPresenterTest {

    private val error = ErrorMessage().apply {
        this.title = "Error"
        this.httpStatus = 500
        this.code = "500"
        this.errorCode = "HTTP_INTERNAL_ERROR"
        this.errorMessage = "Error"
    }

    @Mock
    lateinit var view: PixTermContract.View

    @Mock
    lateinit var repository: PixRepositoryContract

    lateinit var presenter: PixTermPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = spy(PixTermPresenter(view, repository))
    }

    @Test
    fun `Check the success return when the sendTerm is called`() {
        doAnswer {
            (it.arguments[0] as APICallbackDefault<Response<Void>, String>).onSuccess(Response.success(null))
        }.whenever(repository).sendTerm(any())

        presenter.sentTermPix(false)
        verify(view).showLoading()
        verify(presenter).sentTerm()
        verify(view).hideLoading()
        verify(view).successTermPix()

        verify(view, never()).showError(any())
    }

    @Test
    fun `Check the error return when the sendTerm is called`() {
        doAnswer {
            (it.arguments[0] as APICallbackDefault<Response<Void>, String>).onError(error)
        }.whenever(repository).sendTerm(any())

        val captor = argumentCaptor<ErrorMessage>()

        presenter.sentTermPix(false)
        verify(view).showLoading()
        verify(presenter).sentTerm()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).successTermPix()

        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals("HTTP_INTERNAL_ERROR", captor.firstValue.errorCode)
    }

    @Test
    fun `Check the success return when the sendTermPixPartner is called`() {
        doAnswer {
            (it.arguments[0] as APICallbackDefault<Response<Void>, String>).onSuccess(Response.success(null))
        }.whenever(repository).sendTermPixPartner(any())

        presenter.sentTermPix(true)
        verify(view).showLoading()
        verify(presenter).sentTermPixPartner()
        verify(view).hideLoading()
        verify(view).successTermPix()

        verify(view, never()).showError(any())
    }

    @Test
    fun `Check the error return when the sendTermPixPartner is called`() {
        doAnswer {
            (it.arguments[0] as APICallbackDefault<Response<Void>, String>).onError(error)
        }.whenever(repository).sendTermPixPartner(any())

        val captor = argumentCaptor<ErrorMessage>()

        presenter.sentTermPix(true)
        verify(view).showLoading()
        verify(presenter).sentTermPixPartner()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).successTermPix()

        assertEquals(500, captor.firstValue.httpStatus)
        assertEquals("HTTP_INTERNAL_ERROR", captor.firstValue.errorCode)
    }
}