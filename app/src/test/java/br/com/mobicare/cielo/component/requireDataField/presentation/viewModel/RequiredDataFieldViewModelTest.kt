package br.com.mobicare.cielo.component.requireDataField.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.component.requireDataField.utils.RequiredDataFieldFactory
import br.com.mobicare.cielo.component.requireDataField.utils.RequiredDataFieldFactory.fields
import br.com.mobicare.cielo.component.requireDataField.utils.RequiredDataFieldFactory.order
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OrdersResponse
import br.com.mobicare.cielo.component.requiredDataField.domain.useCase.PostUpdateDataRequiredDataFieldUseCase
import br.com.mobicare.cielo.component.requiredDataField.presentation.viewmodel.RequiredDataFieldViewModel
import br.com.mobicare.cielo.component.requiredDataField.utils.RequiredDataFieldConstants.REQUIRED_DATA_FIELD_INVALID_DATA_ERROR
import br.com.mobicare.cielo.component.requiredDataField.utils.UiRequiredDataFieldState
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequiredDataFieldViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postUpdateDataRequiredDataFieldUseCase =
        mockk<PostUpdateDataRequiredDataFieldUseCase>()

    private lateinit var viewModel: RequiredDataFieldViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    private val resultSuccess =
        CieloDataResult.Success(OrdersResponse(orderId = RequiredDataFieldFactory.orderId))
    private val resultGenericError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmptyError = CieloDataResult.Empty()
    private val resultInvalidDataError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(
                flagErrorCode = REQUIRED_DATA_FIELD_INVALID_DATA_ERROR
            )
        )
    )
    private val errorMessageToken = NewErrorMessage(flagErrorCode = Text.OTP)
    private val resultMfaTokenError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
            newErrorMessage = errorMessageToken
        )
    )

    @Before
    fun setup() {
        viewModel = RequiredDataFieldViewModel(
            getUserObjUseCase,
            postUpdateDataRequiredDataFieldUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess
    }

    @Test
    fun `it should set UIRequiredDataFieldState as Success when update required data`() = runTest {
        coEvery {
            postUpdateDataRequiredDataFieldUseCase(any(), any())
        } returns resultSuccess

        viewModel.sendDataField(context, DEFAULT_OTP, fields, order)

        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.requiredDataFieldState.value).isInstanceOf(UiRequiredDataFieldState.Success::class.java)
    }

    @Test
    fun `it should set UIRequiredDataFieldState as GenericError when update required data was Empty`() =
        runTest {
            coEvery {
                postUpdateDataRequiredDataFieldUseCase(any(), any())
            } returns resultEmptyError

            viewModel.sendDataField(context, DEFAULT_OTP, fields, order)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.requiredDataFieldState.value).isInstanceOf(UiRequiredDataFieldState.GenericError::class.java)
        }

    @Test
    fun `it should set UIRequiredDataFieldState as GenericError when update required data was generic error`() =
        runTest {
            coEvery {
                postUpdateDataRequiredDataFieldUseCase(any(), any())
            } returns resultGenericError

            viewModel.sendDataField(context, DEFAULT_OTP, fields, order)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.requiredDataFieldState.value).isInstanceOf(UiRequiredDataFieldState.GenericError::class.java)
        }

    @Test
    fun `it should set UIRequiredDataFieldState as InvalidDataError when update required data was invalid data error`() =
        runTest {
            coEvery {
                postUpdateDataRequiredDataFieldUseCase(any(), any())
            } returns resultInvalidDataError

            viewModel.sendDataField(context, DEFAULT_OTP, fields, order)

            dispatcherRule.advanceUntilIdle()

            val state = viewModel.requiredDataFieldState.value

            assertThat(state).isInstanceOf(UiRequiredDataFieldState.InvalidDataError::class.java)
            val stateData = state as UiRequiredDataFieldState.InvalidDataError
            assertEquals(
                R.string.required_data_field_error_invalid_data_message,
                stateData.message
            )
        }

    @Test
    fun `it should set UIRequiredDataFieldState as TokenError when update required data was token error`() =
        runTest {
            coEvery {
                postUpdateDataRequiredDataFieldUseCase(any(), any())
            } returns resultMfaTokenError

            viewModel.sendDataField(context, DEFAULT_OTP, fields, order)

            dispatcherRule.advanceUntilIdle()

            val state = viewModel.requiredDataFieldState.value

            assertThat(state).isInstanceOf(UiRequiredDataFieldState.TokenError::class.java)
            val stateData = state as UiRequiredDataFieldState.TokenError
            assertEquals(errorMessageToken, stateData.error)
        }

}