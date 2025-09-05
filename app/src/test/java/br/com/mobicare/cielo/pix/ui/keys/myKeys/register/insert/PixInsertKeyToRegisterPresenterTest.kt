package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.ValidateCode
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val EMAIL_MOCK = "teste@cielo.com"
private const val PHONE_MOCK = "(12) 99711-1111"

class PixInsertKeyToRegisterPresenterTest {

    @Mock
    lateinit var view: PixInsertKeyToRegisterContract.View

    @Mock
    lateinit var repository: PixKeysRepositoryContract

    private lateinit var presenter: PixInsertKeyToRegisterPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixInsertKeyToRegisterPresenter(
            view,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `when call onSendValidationCode for email and get success return, show onSuccessSendCode`() {
        val captor = argumentCaptor<ValidateCode>()
        val response = ValidateCode(
            key = EMAIL_MOCK,
            keyType = PixKeyTypeEnum.EMAIL.name
        )

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .requestValidateCode(captor.capture())

        presenter.onSendValidationCode(EMAIL_MOCK, PixKeyTypeEnum.EMAIL)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessSendCode()
        verify(view, never()).showError(any())

        assertEquals(EMAIL_MOCK, captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.EMAIL.name, captor.firstValue.keyType)
    }

    @Test
    fun `when call onSendValidationCode for email and get an error return show showError`() {
        val captor = argumentCaptor<ValidateCode>()
        val captorError = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .requestValidateCode(captor.capture())

        presenter.onSendValidationCode(EMAIL_MOCK, PixKeyTypeEnum.EMAIL)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture())
        verify(view, never()).onSuccessSendCode()

        assertEquals(EMAIL_MOCK, captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.EMAIL.name, captor.firstValue.keyType)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when call onSendValidationCode for phone and get success return, show onSuccessSendCode`() {
        val captor = argumentCaptor<ValidateCode>()
        val response = ValidateCode(
            key = "+5512997111111",
            keyType = PixKeyTypeEnum.PHONE.name
        )

        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .requestValidateCode(captor.capture())

        presenter.onSendValidationCode(PHONE_MOCK, PixKeyTypeEnum.PHONE)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessSendCode()
        verify(view, never()).showError(any())

        assertEquals("+5512997111111", captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.PHONE.name, captor.firstValue.keyType)
    }

    @Test
    fun `when call onSendValidationCode for phone and get an error return show showError`() {
        val captor = argumentCaptor<ValidateCode>()
        val captorError = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .requestValidateCode(captor.capture())

        presenter.onSendValidationCode(PHONE_MOCK, PixKeyTypeEnum.PHONE)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture())
        verify(view, never()).onSuccessSendCode()

        assertEquals("+5512997111111", captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.PHONE.name, captor.firstValue.keyType)
        assertEquals(500, captorError.firstValue.httpStatus)
    }
}