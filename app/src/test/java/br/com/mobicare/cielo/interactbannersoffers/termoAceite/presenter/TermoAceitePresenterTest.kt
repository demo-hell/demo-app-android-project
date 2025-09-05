package br.com.mobicare.cielo.interactbannersoffers.termoAceite.presenter

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerRepository
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.TermoAceiteContract
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.TermoAceitePresenter
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TermoAceitePresenterTest {
    @Mock
    lateinit var view: TermoAceiteContract.View

    @Mock
    lateinit var repository: InteractBannerRepository

    private lateinit var presenter: TermoAceitePresenter

    private val ioScheduler = Schedulers.trampoline()
    private val uiScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = TermoAceitePresenter(view, repository, uiScheduler, ioScheduler)
    }

    @Test
    fun `Success on POST to accept the terms`() {
        val success = retrofit2.Response.success(200)
        val successObservable = Observable.just(success)

        doReturn(successObservable).whenever(repository).postTermoAceite(1956783)

        presenter.submitTermoAceite(1956783)

        verify(view).showSuccess()
        verify(view, never()).showError()
    }

    @Test
    fun `Error on POST to accept the terms`() {
        val retrofitException = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(retrofitException)
        doReturn(errorObservable).whenever(repository).postTermoAceite(55255003)

        presenter.submitTermoAceite(55255003)

        verify(view).showError()
        verify(view, never()).showSuccess()
    }
}