package br.com.mobicare.cielo.mfa.activation

import br.com.mobicare.cielo.commons.constants.MFA_USER_BLOCKED
import br.com.mobicare.cielo.commons.constants.MFA_WRONG_VERIFICATION_CODE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractor
import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.InOrder
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.Callable

class PutValuePresenterImplTest {

    @Mock
    lateinit var view: PutValueView

    @Mock
    lateinit var putValueInteractor: PutValueInteractor

    lateinit var presenter: PutValuePresenterImpl

    private val activationCodeValue1 = "0.11"
    private val activationCodeValue2 = "0.11"

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { h: Callable<Scheduler?>? -> Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { h: Scheduler? -> Schedulers.trampoline() }

        MockitoAnnotations.initMocks(this)

        presenter = PutValuePresenterImpl(view, putValueInteractor)
    }

    @Test
    fun `Test onCreate()`() {
        presenter.onCreate()

        inOrder(view) {
            verify(view).initExplanationSpannable()
            verify(view).initTextChange()

            verify(view, never()).tokenLostWarning()
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `Test onCreate() with ActiveMfaUser`() {
        doReturn(true)
            .whenever(putValueInteractor).hasActiveMfaUser()

        presenter.onCreate()

        inOrder(view) {
            verify(view).initExplanationSpannable()
            verify(view).initTextChange()
            verify(view).tokenLostWarning()

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `Check success on activationCode()`() {
        val putValueResponse: PutValueResponse = mock()

        doAnswer {
            Observable.just(putValueResponse)
        }.whenever(putValueInteractor).activationCode(any())

        presenter.activationCode(activationCodeValue1, activationCodeValue2)

        inOrder(view, putValueInteractor) {
            verify(view).showLoading()
            verify(putValueInteractor).saveMfaUserInformation(putValueResponse)
            verify(view).onValueSuccess()
            verify(view).hideLoading()

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `Check error on activationCode() 420 MFA_WRONG_VERIFICATION_CODE`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
            errorCode = MFA_WRONG_VERIFICATION_CODE
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).incorrectValues()
        }
    }

    @Test
    fun `Check error on activationCode() 420 MFA_USER_BLOCKED`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
            errorCode = MFA_USER_BLOCKED
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).incorrectValuesThirdAttempt()
        }
    }

    @Test
    fun `Check error on activationCode() 420 no errorCode`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 420
            errorCode = ""
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).onValueError(argThat {
                errorMessage.errorCode == errorCode && errorMessage.httpStatus == httpStatus
            })
        }
    }

    @Test
    fun `Check error on activationCode() 400`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 400
            errorCode = ""
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).onInvalidRequestError(argThat {
                errorMessage.httpStatus == httpStatus && errorMessage.errorCode == errorCode
            })
        }
    }

    @Test
    fun `Check error on activationCode() 404`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 404
            errorCode = ""
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).onBusinessError(argThat {
                errorMessage.httpStatus == httpStatus && errorMessage.errorCode == errorCode
            })
        }
    }

    @Test
    fun `Check error on activationCode() other httpStatus`() {
        val errorMessage = ErrorMessage().apply {
            httpStatus = 500
            errorCode = ""
        }

        `Check error on activationCode()`(errorMessage) {
            it.verify(view).onValueError(argThat {
                errorMessage.errorCode == errorCode && errorMessage.httpStatus == httpStatus
            })
        }
    }

    private fun `Check error on activationCode()`(errorMessage: ErrorMessage, verification: (inOrder: InOrder) -> Unit) {
        doAnswer {
            observableException(errorMessage)
        }.whenever(putValueInteractor).activationCode(any())

        presenter.activationCode(activationCodeValue1, activationCodeValue2)

        inOrder(view) {
            verify(view).showLoading()
            verification(this)
            verify(view).hideLoading()

            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `Check fetchEnrollmentActiveBank show bank`() {
        val enrollmentBankResponse: EnrollmentBankResponse = mock()

        doAnswer {
            Observable.just(enrollmentBankResponse)
        }.whenever(putValueInteractor).fetchActiveBank()

        presenter.fetchEnrollmentActiveBank()

        verify(view).configureActiveBank(enrollmentBankResponse)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Check fetchEnrollmentActiveBank hide bank`() {
        doAnswer {
            observableException(ErrorMessage())
        }.whenever(putValueInteractor).fetchActiveBank()

        presenter.fetchEnrollmentActiveBank()

        verify(view).hideEnrollmentActiveBank()
        verifyNoMoreInteractions(view)
    }

    private fun observableException(errorMessage: ErrorMessage): Observable<RetrofitException> {
        val exception = RetrofitException(
            null,
            null,
            APIUtils.createResponse(
                errorMessage
            ),
            RetrofitException.Kind.HTTP,
            null,
            null,
            errorMessage.httpStatus)

        return Observable.error(exception)
    }
}