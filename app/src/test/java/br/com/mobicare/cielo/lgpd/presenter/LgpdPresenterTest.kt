package br.com.mobicare.cielo.lgpd.presenter

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.lgpd.LgpdContract
import br.com.mobicare.cielo.lgpd.LgpdPresenter
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Response


class LgpdPresenterTest {

    @Mock
    lateinit var view: LgpdContract.View

    @Mock
    lateinit var repository: LgpdRepository

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: LgpdPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = LgpdPresenter(view, repository, uiScheduler, ioScheduler)
    }

    @Test
    fun `Validate process of treatment of the error 500 at return in the endpoint LGPD`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).postLgpdAgreement()

        presenter.onAgreeButtonClicked()

        val captor = argumentCaptor<LgpdPresenter.State>()

        verify(view, times(2)).render(captor.capture())


        val state = captor.secondValue as LgpdPresenter.State.Error
        assertEquals(captor.firstValue, LgpdPresenter.State.Loading)
        assertEquals(captor.secondValue, LgpdPresenter.State.Error)
        assertEquals(state.errorMessage?.httpStatus, 500)


    }

    @Test
    fun `Validate the return of success in the endpoint LGPD`() {
        val response = Response.success(200)

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(repository).postLgpdAgreement()

        presenter.onAgreeButtonClicked()

        val captorLoading = argumentCaptor<LgpdPresenter.State>()

        verify(view).render(captorLoading.capture())
        verify(view).showMainWindow(anyOrNull(), any())

        assertEquals(captorLoading.firstValue, LgpdPresenter.State.Loading)
    }
}