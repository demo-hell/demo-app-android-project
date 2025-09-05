package br.com.mobicare.cielo.pix.ui.mylimits.timemanagement

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest
import br.com.mobicare.cielo.pix.domain.PixTimeManagementResponse
import br.com.mobicare.cielo.pix.ui.mylimits.utils.MyLimitsTestFactory
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

private const val OTP = "0000"
private const val TEN = "20:00:00"

class PixMyLimitsTimeManagementPresenterTest {

    @Mock
    lateinit var view: PixMyLimitsTimeManagementContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var repository: PixTimeManagementRepositoryContract

    private lateinit var presenter: PixMyLimitsTimeManagementPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val timeManagementResponse = MyLimitsTestFactory.timeManagementResponse

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixMyLimitsTimeManagementPresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `When getUsername is called and value is empty, the return must be empty`() {
        doReturn("").whenever(userPreferences).userName
        Assert.assertEquals("", presenter.getUsername())
    }

    @Test
    fun `When getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("testeCielo").whenever(userPreferences).userName
        Assert.assertEquals("testeCielo", presenter.getUsername())
    }

    @Test
    fun `when call getNightTime and have a success return show onSuccessGetNightTime`() {
        val captor = argumentCaptor<PixTimeManagementResponse>()

        val response =
            Gson().fromJson(timeManagementResponse, PixTimeManagementResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .getNightTime()

        presenter.getNightTime()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessGetNightTime(captor.capture())

        verify(view, never()).onErrorGetNightTime(any())
        verify(view, never()).showError(any())
        verify(view, never()).onErrorUpdateNightTime(any())
        verify(view, never()).onSuccessUpdateNightTime()


        assertTrue(captor.allValues.contains(response))
    }

    @Test
    fun `when call getNightTime and get error return show onErrorGetNightTime`() {
        val captor = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val error = Observable.error<RetrofitException>(exception)
        doReturn(error).whenever(repository)
            .getNightTime()

        presenter.getNightTime()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorGetNightTime(captor.capture())

        verify(view, never()).onSuccessUpdateNightTime()
        verify(view, never()).showError(any())
        verify(view, never()).onErrorUpdateNightTime(any())
        verify(view, never()).onSuccessUpdateNightTime()


        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when call onUpdateNightTime and have a success return show onSuccessUpdateNightTime`() {
        val captorOTP = argumentCaptor<String>()
        val captor = argumentCaptor<PixTimeManagementRequest>()

        val success = Observable.just(retrofit2.Response.success(200))
        doReturn(success).whenever(repository)
            .updateNightTime(captorOTP.capture(), captor.capture())

        presenter.onUpdateNightTime(OTP, TEN)


        verify(view).onSuccessUpdateNightTime()

        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onSuccessGetNightTime(any())
        verify(view, never()).onErrorGetNightTime(any())
        verify(view, never()).showError(any())
        verify(view, never()).onErrorUpdateNightTime(any())

        assertEquals(OTP, captorOTP.firstValue)
        assertEquals(TEN, captor.firstValue.nighttimeStart)
    }

    @Test
    fun `when call onUpdateNightTime and get error 500 return show onErrorUpdateNightTime`() {
        val captorOTP = argumentCaptor<String>()
        val captorError = argumentCaptor<ErrorMessage>()
        val captor = argumentCaptor<PixTimeManagementRequest>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val error = Observable.error<RetrofitException>(exception)
        doReturn(error).whenever(repository)
            .updateNightTime(captorOTP.capture(), captor.capture())

        `when`(view.onErrorUpdateNightTime(any())).then { invocation ->
            (invocation.arguments[0] as? () -> Unit)?.invoke()
        }

        presenter.onUpdateNightTime(OTP, TEN)

        verify(view).onErrorUpdateNightTime(any())
        verify(view).showError(captorError.capture())


        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onSuccessUpdateNightTime()
        verify(view, never()).onSuccessGetNightTime(any())
        verify(view, never()).onErrorGetNightTime(any())

        assertEquals(OTP, captorOTP.firstValue)
        assertEquals(TEN, captor.firstValue.nighttimeStart)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when call onUpdateNightTime and get error 420 return show onErrorUpdateNightTime`() {
        val captorOTP = argumentCaptor<String>()
        val captorError = argumentCaptor<ErrorMessage>()
        val captor = argumentCaptor<PixTimeManagementRequest>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 420
        )

        val error = Observable.error<RetrofitException>(exception)
        doReturn(error).whenever(repository)
            .updateNightTime(captorOTP.capture(), captor.capture())

        `when`(view.onErrorUpdateNightTime(any())).then { invocation ->
            (invocation.arguments[0] as? () -> Unit)?.invoke()
        }

        presenter.onUpdateNightTime(OTP, TEN)

        verify(view).onErrorUpdateNightTime(any())
        verify(view).showError(captorError.capture())
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onSuccessUpdateNightTime()
        verify(view, never()).onSuccessGetNightTime(any())
        verify(view, never()).onErrorGetNightTime(any())


        assertEquals(OTP, captorOTP.firstValue)
        assertEquals(TEN, captor.firstValue.nighttimeStart)
        assertEquals(420, captorError.firstValue.httpStatus)
    }
}