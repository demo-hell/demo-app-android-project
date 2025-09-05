package br.com.mobicare.cielo.mfa.resume.presenter

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.tryCast
import br.com.mobicare.cielo.mfa.BankEnrollmentResponse
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA
import br.com.mobicare.cielo.mfa.resume.ResumeBankAndCNPJContract
import br.com.mobicare.cielo.mfa.resume.ResumeBankAndCNPJPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


class ResumeBankAndCNPJPresenterTest {

    private val mfaAccount = MfaAccount(bankName = "Banco do Brasil S.A",
            bankCode = "001",
            agency = "1234",
            account = "56789",
            accountDigit = "56789",
            accountType = "CC",
            imgSource = "",
            legalEntity = "JURIDICA",
            identificationNumber = "44883268000194"
    )

    @Mock
    lateinit var view: ResumeBankAndCNPJContract.View

    @Mock
    lateinit var repository: MfaRepository

    lateinit var presenter: ResumeBankAndCNPJPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = ResumeBankAndCNPJPresenter(view, repository)
    }

    @Test
    fun `Check the success return on sendEnrollment for token configuration`() {
        val response = BankEnrollmentResponse(EnrollmentStatus.ACTIVE.status)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<BankEnrollmentResponse, String>> {
                this.onSuccess(response)
            }
        }.whenever(repository).postBankEnrollment(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()

        presenter.sendEnrollment(mfaAccount)
        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showSuccessful()
        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
    }

    @Test
    fun `Check PENNY_DROP_TEMPORARILY_BLOCKED error when calling sendEnrollment for token setting`() {
        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 403
            code = "403"
            errorCode = EnrollmentStatus.PENNY_DROP_TEMPORARILY_BLOCKED.name
            errorMessage = EnrollmentStatus.PENNY_DROP_TEMPORARILY_BLOCKED.name
        }

        val response = APIUtils.createResponse(errorMessage)
        val exception = RetrofitException(
                message = null,
                url = null,
                response = response,
                kind = RetrofitException.Kind.HTTP,
                exception = null,
                retrofit = null,
                httpStatus = 403)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<BankEnrollmentResponse, String>> {
                this.onError(APIUtils.convertToErro(exception))
            }
        }.whenever(repository).postBankEnrollment(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        presenter.sendEnrollment(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showTemporarilyBlockError(captorError.capture())

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
        assertEquals(captorError.firstValue.errorCode, EnrollmentStatus.PENNY_DROP_TEMPORARILY_BLOCKED.name)
        assertEquals(captorError.firstValue.httpStatus, 403)
    }

    @Test
    fun `Check error return when calling sendEnrollment for token configuration when code error is different from PENNY_DROP_TEMPORARILY_BLOCKED`() {
        val exception = RetrofitException(
                message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.HTTP,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<BankEnrollmentResponse, String>> {
                this.onError(APIUtils.convertToErro(exception))
            }
        }.whenever(repository).postBankEnrollment(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        presenter.sendEnrollment(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showError(captorError.capture())

        assertEquals(true, captor.firstValue)
        assertEquals(false, captor.secondValue)
        assertEquals("Infelizmente ocorreu algum erro. Por favor, tente novamente.", captorError.firstValue.message)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `Check PENNY_DROP_TEMPORARILY_BLOCKED error when calling sendChallenge for token setting`() {
        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 403
            code = "403"
            errorCode = MerchantStatusMFA.PENNY_DROP_TEMPORARILY_BLOCKED.name
            errorMessage = MerchantStatusMFA.PENNY_DROP_TEMPORARILY_BLOCKED.name
        }

        val response = APIUtils.createResponse(errorMessage)
        val exception = RetrofitException(
                message = null,
                url = null,
                response = response,
                kind = RetrofitException.Kind.HTTP,
                exception = null,
                retrofit = null,
                httpStatus = 403)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<BankEnrollmentResponse, String>> {
                this.onError(APIUtils.convertToErro(exception))
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showTemporarilyBlockError(captorError.capture())

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
        assertEquals(captorError.firstValue.errorCode, MerchantStatusMFA.PENNY_DROP_TEMPORARILY_BLOCKED.name)
        assertEquals(captorError.firstValue.httpStatus, 403)
    }

    @Test
    fun `Check error return when calling sendChallenge for token configuration when code error is different from PENNY_DROP_TEMPORARILY_BLOCKED`() {
        val exception = RetrofitException(
                message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.HTTP,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<BankEnrollmentResponse, String>> {
                this.onError(APIUtils.convertToErro(exception))
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showError(captorError.capture())

        assertEquals(true, captor.firstValue)
        assertEquals(false, captor.secondValue)
        assertEquals("Infelizmente ocorreu algum erro. Por favor, tente novamente.", captorError.firstValue.message)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `Check the return of ACTIVE when sendChallenge is called to set the token`() {
        val response = EnrollmentResponse(status = EnrollmentStatus.ACTIVE.status,
                type = null,
                typeCode = null,
                statusCode = null,
                statusTrace = null)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<EnrollmentResponse, String>> {
                this.onSuccess(response)
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showBankChallengeActive()

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
    }

    @Test
    fun `Check the return of WAITING_ACTIVATION when sendChallenge is called to set the token`() {
        val response = EnrollmentResponse(status = EnrollmentStatus.WAITING_ACTIVATION.status,
                type = null,
                typeCode = null,
                statusCode = null,
                statusTrace = null)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<EnrollmentResponse, String>> {
                this.onSuccess(response)
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showBankChallengePending()

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
    }

    @Test
    fun `Check the return of BLOCKED when sendChallenge is called to set the token`() {
        val response = EnrollmentResponse(status = EnrollmentStatus.BLOCKED.status,
                type = null,
                typeCode = null,
                statusCode = null,
                statusTrace = null)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<EnrollmentResponse, String>> {
                this.onSuccess(response)
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showBlocked()

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
    }

    @Test
    fun `check sendChallenge behavior when return is success and status is null`() {
        val response = EnrollmentResponse(status = null,
                type = null,
                typeCode = null,
                statusCode = null,
                statusTrace = null)

        doAnswer {
            it.arguments[1].tryCast<APICallbackDefault<EnrollmentResponse, String>> {
                this.onSuccess(response)
            }
        }.whenever(repository).sendMFABankChallenge(
                account = eq(mfaAccount),
                callback = any()
        )

        val captor = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        presenter.sendChallenge(mfaAccount)

        verify(view, times(2)).showLoading(captor.capture())
        verify(view).showError(captorError.capture())

        assertEquals(captor.firstValue, true)
        assertEquals(captor.secondValue, false)
        assertEquals(captorError.firstValue.message, "Infelizmente ocorreu algum erro. Por favor, tente novamente.")
        assertEquals(captorError.firstValue.httpStatus, 500)
    }

}