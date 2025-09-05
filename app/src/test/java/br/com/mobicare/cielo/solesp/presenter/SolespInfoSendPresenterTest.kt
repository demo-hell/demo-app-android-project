package br.com.mobicare.cielo.solesp.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.solesp.api.SolespRepository
import br.com.mobicare.cielo.solesp.domain.SolespRequest
import br.com.mobicare.cielo.solesp.domain.SolespResponse
import br.com.mobicare.cielo.solesp.model.SolespModel
import br.com.mobicare.cielo.solesp.ui.infoSend.SolespInfoSendContract
import br.com.mobicare.cielo.solesp.ui.infoSend.SolespInfoSendPresenter
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SolespInfoSendPresenterTest {

    @Mock
    lateinit var view: SolespInfoSendContract.View

    @Mock
    lateinit var repository: SolespRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: SolespInfoSendPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = SolespInfoSendPresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `success send solesp request`() {
        val success = SolespResponse()
        val successObservable = Observable.just(success)
        doReturn(successObservable).whenever(repository).sendSolespRequest(SolespRequest())

        presenter.sendSolespRequest(SolespModel())

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showSuccess()
        verify(view, never()).showError()
    }

    @Test
    fun `error send solesp request`() {
        val exception = RetrofitException(
            message = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )
        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).sendSolespRequest(SolespRequest())

        presenter.sendSolespRequest(SolespModel())

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view, never()).showSuccess()
        verify(view).showError()
    }

}