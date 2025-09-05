package br.com.mobicare.cielo.mfa.router

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.utils.tryCast
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MfaRouterPresenterTest {

    @Mock
    lateinit var view: MfaRouterContract.View

    @Mock
    lateinit var repository: MfaRepository

    @Mock
    lateinit var userInformationRepository: UserInformationRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    lateinit var presenter: MfaRouterPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        presenter = MfaRouterPresenter(
            view,
            uiScheduler,
            ioScheduler,
            repository,
            userInformationRepository,
            userPreferences,
            featureTogglePreference
        )
    }

    @Test
    fun `Check error on checkIsMfaEligible()`() {
        val error = ErrorMessage().apply { httpStatus = 500 }
        doAnswer {
            it.arguments[0].tryCast<APICallbackDefault<MfaEligibilityResponse, String>> {
                this.onError(error)
            }
        }.whenever(repository).checkEligibility(callback = any())

        presenter.checkIsMfaEligible()

        inOrder(view).run {
            verify(view).showLoading(true)
            verify(view).showLoading(false)

            verify(view).showError(argThat { this.httpStatus == 500 })

            verifyNoMoreInteractions(view)
        }
    }

    @Test
    fun `Check error on load(isEnrollment = false) MFA Eligibility http error`() {
        val error = ErrorMessage().apply { httpStatus = 500 }
        doAnswer {
            it.arguments[0].tryCast<APICallbackDefault<MfaEligibilityResponse, String>> {
                this.onError(error)
            }
        }.whenever(repository).checkEligibility(callback = any())

        presenter.load(isEnrollment = false)

        inOrder(view).run {
            verify(view).showLoading(true)
            verify(view).showLoading(false)

            verify(view).showError(argThat { this.httpStatus == 500 })

            verifyNoMoreInteractions(view)
        }
    }

    private fun getEnrollmentResponse(
        status: String = "",
        type: String = "",
        typeCode: Int = 0,
        statusCode: String = "",
        statusTrace: String = ""
    ): EnrollmentResponse {
        return EnrollmentResponse(
            status = status,
            type = type,
            typeCode = typeCode,
            statusCode = statusCode,
            statusTrace = statusTrace
        )
    }

    private fun getMfaEligibilityResponse(
        status: String = "",
        type: String = "",
        typeCode: Int = 0,
        statusCode: String = "",
        statusTrace: String = ""
    ): MfaEligibilityResponse {
        return MfaEligibilityResponse(
            status = status,
            type = type,
            typeCode = typeCode,
            statusCode = statusCode,
            statusTrace = statusTrace
        )
    }
}