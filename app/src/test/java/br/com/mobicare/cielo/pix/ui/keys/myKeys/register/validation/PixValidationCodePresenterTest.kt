package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.enums.PixRevokeClaimsEnum
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

private const val EMAIL_MOCK = "teste@cielo.com"
private const val PHONE_MOCK = "(12) 99711-1111"
private const val CODE_MOCK = "12345"
private const val OTP_MOCK = "000000"
private const val CLAIM_ID_MOCK = "62de8a02-3563-4b49-b734-45dea5b0d7c9"

class PixValidationCodePresenterTest {

    private val response = "{\n" +
            "  \"claimId\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
            "  \"claimStatus\": \"OPENED\",\n" +
            "  \"confirmationReason\": \"CLIENT_SOLICITATION\",\n" +
            "  \"lastModificationDate\": \"2022-02-11T14:55:16.258Z\"\n" +
            "}"

    @Mock
    lateinit var view: PixValidationCodeContract.View

    @Mock
    lateinit var repository: PixKeysRepositoryContract

    @Mock
    lateinit var claimRepository: PixClaimRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixValidationCodePresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixValidationCodePresenter(
            view,
            repository,
            claimRepository,
            userPreferences,
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

        presenter.onSendValidationCode(EMAIL_MOCK, PixKeyTypeEnum.EMAIL, isClaimFlow = false)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessSendCode()
        verify(view, never()).showError(any())

        assertEquals(EMAIL_MOCK, captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.EMAIL.name, captor.firstValue.keyType)
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

        presenter.onSendValidationCode(PHONE_MOCK, PixKeyTypeEnum.PHONE, isClaimFlow = false)

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

        presenter.onSendValidationCode(PHONE_MOCK, PixKeyTypeEnum.PHONE, isClaimFlow = false)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture())
        verify(view, never()).onSuccessSendCode()

        assertEquals("+5512997111111", captor.firstValue.key)
        assertEquals(PixKeyTypeEnum.PHONE.name, captor.firstValue.keyType)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when call onRevokeClaim and return an error show showError`() {
        val captorOTP = argumentCaptor<String>()
        val captorRequest = argumentCaptor<RevokeClaimsRequest>()
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
        doReturn(errorObservable).whenever(claimRepository)
            .revokeClaims(captorOTP.capture(), captorRequest.capture())

        presenter.onRevokeClaim(OTP_MOCK, CLAIM_ID_MOCK, CODE_MOCK)

        verify(view).onShowErrorClaim(captorError.capture())


        verify(view, never()).showError(any())
        verify(view, never()).onSuccessSendCode()
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()
        verify(view, never()).onSuccessRevokeClaim()
        verify(view, never()).onSuccessClaim(any())

        assertEquals(OTP_MOCK, captorOTP.firstValue)
        assertEquals(CLAIM_ID_MOCK, captorRequest.firstValue.claimId)
        assertEquals(PixRevokeClaimsEnum.FRAUD.name, captorRequest.firstValue.reason)
        assertEquals(false, captorRequest.firstValue.isClaimer)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when call onRevokeClaim and return a success show onSuccessClaim and onSuccessRevokeClaim`() {
        val captorOTP = argumentCaptor<String>()
        val captorRequest = argumentCaptor<RevokeClaimsRequest>()

        val response = Gson().fromJson(response, RevokeClaimsResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(claimRepository)
            .revokeClaims(captorOTP.capture(), captorRequest.capture())

        `when`(view.onSuccessClaim(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onRevokeClaim(OTP_MOCK, CLAIM_ID_MOCK, CODE_MOCK)

        verify(view).onSuccessClaim(any())
        verify(view).onSuccessRevokeClaim()

        verify(view, never()).onShowErrorClaim(any())
        verify(view, never()).showError(any())
        verify(view, never()).onSuccessSendCode()
        verify(view, never()).showLoading()
        verify(view, never()).hideLoading()

        assertEquals(OTP_MOCK, captorOTP.firstValue)
        assertEquals(CLAIM_ID_MOCK, captorRequest.firstValue.claimId)
        assertEquals(PixRevokeClaimsEnum.FRAUD.name, captorRequest.firstValue.reason)
        assertEquals(false, captorRequest.firstValue.isClaimer)
    }
}