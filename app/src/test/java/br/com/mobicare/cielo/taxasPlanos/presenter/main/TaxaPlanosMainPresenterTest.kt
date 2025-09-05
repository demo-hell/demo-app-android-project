package br.com.mobicare.cielo.taxasPlanos.presenter.main

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosStatusPlanResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TaxaPlanosMainPresenterTest {

    private val feeMachine = TaxaPlanosMachine(
        model = "",
        logicalNumber = "123",
        logicalNumberDigit = "1",
        rentalAmount = 40.8,
        name = "Cielo",
        description = "",
        technology = "",
        replacementAllowed = true
    )

    private val responseStatus = TaxaPlanosStatusPlanResponse(planName = "DO_SEU_JEITO")

    private val responseMachine = TaxaPlanosSolutionResponse(
        pos = listOf(feeMachine, feeMachine),
        mobile = listOf(feeMachine)
    )

    @Mock
    lateinit var view: TaxaPlanosMainContract.View

    @Mock
    lateinit var repository: TaxaPlanoRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    lateinit var presenter: TaxaPlanosMainPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = TaxaPlanosMainPresenter(repository, view, userPreferences)
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Success on loadStatusPlan and loadMachine call`() {
        val captorStatus = argumentCaptor<TaxaPlanosStatusPlanResponse>()
        val captorMachine = argumentCaptor<TaxaPlanosSolutionResponse>()

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosStatusPlanResponse, String>).onSuccess(
                responseStatus
            )
        }.whenever(repository).loadStatusPlan(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosSolutionResponse, String>).onSuccess(
                responseMachine
            )
        }.whenever(repository).loadMachine(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        presenter.loadData()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view, never()).onLogout()
        verify(view, never()).onError(any())
        verify(view).showResult(captorStatus.capture(), captorMachine.capture())

        assertEquals("DO_SEU_JEITO", captorStatus.firstValue.planName)
        assertTrue(captorMachine.allValues.contains(responseMachine))
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun `Error on loadStatusPlan call and success on loadMachine call`() {
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

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosSolutionResponse, String>).onSuccess(
                responseMachine
            )
        }.whenever(repository).loadMachine(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosStatusPlanResponse, String>).onError(
                APIUtils.convertToErro(exception)
            )
        }.whenever(repository).loadStatusPlan(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        presenter.loadData()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onError(captorError.capture())
        verify(view, never()).onLogout()
        verify(view, never()).showResult(any(), any())

        assertEquals(500, captorError.firstValue.httpStatus)
    }


    @Test
    @Suppress("UNCHECKED_CAST")
    fun `error 401 on loadStatusPlan call and success on loadMachine call`() {
        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 401
            code = "401"
            logout = true
        }
        val response = APIUtils.createResponse(errorMessage)
        val exception = RetrofitException(
            message = null,
            url = null,
            response = response,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 401
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosSolutionResponse, String>).onError(
                APIUtils.convertToErro(exception)
            )
        }.whenever(repository).loadMachine(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosStatusPlanResponse, String>).onError(
                APIUtils.convertToErro(exception)
            )
        }.whenever(repository).loadStatusPlan(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        presenter.loadData()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view, never()).onError(any())
        verify(view, never()).showResult(any(), any())
        verify(view).onLogout()
    }
}