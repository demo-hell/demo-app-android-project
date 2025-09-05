package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.useCase.PutChargebackAcceptUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackPendingDetailsViewModel
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.chargeback.utils.UiAcceptState
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChargebackPendingDetailsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val putChargebackAcceptUseCase = mockk<PutChargebackAcceptUseCase>()
    private val userObjUseCase = mockk<GetUserObjUseCase>()
    private val context = mockk<Context>(relaxed = true)

    private val acceptResponse = ChargebackFactory.acceptResponse
    private val validChargeback = ChargebackFactory.chargebackWithIdAndMerchantId
    private val invalidChargeback = ChargebackFactory.chargebackEmpty

    private val resultSuccess = CieloDataResult.Success(acceptResponse)
    private val resultEmpty = CieloDataResult.Empty()
    private val resultNetworkError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultMfaTokenError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
            newErrorMessage = NewErrorMessage(flagErrorCode = Text.OTP)
        )
    )
    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    private lateinit var viewModel: ChargebackPendingDetailsViewModel

    @Before
    fun setUp() {
        viewModel = ChargebackPendingDetailsViewModel(putChargebackAcceptUseCase, userObjUseCase)
    }

    @Test
    fun `it should set success state on success result of chargebackAccept call`() = runTest {
        // given
        coEvery { putChargebackAcceptUseCase(any(), any()) } returns resultSuccess

        // when
        viewModel.chargebackAccept(context, EMPTY, validChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.Success)
    }

    @Test
    fun `it should set success state on empty result of chargebackAccept call`() = runTest {
        // given
        coEvery { putChargebackAcceptUseCase(any(), any()) } returns resultEmpty

        // when
        viewModel.chargebackAccept(context, EMPTY, validChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.Success)
    }

    @Test
    fun `it should set error state when setting an invalid chargeback data`() = runTest {
        // when
        viewModel.chargebackAccept(context, EMPTY, invalidChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.Error)
    }

    @Test
    fun `it should set error state on error result and null context of chargebackAccept call`() = runTest {
        // given
        coEvery { putChargebackAcceptUseCase(any(), any()) } returns resultNetworkError

        // when
        viewModel.chargebackAccept(null, EMPTY, validChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.Error)
    }

    @Test
    fun `it should set error state on network error result of chargebackAccept call`() = runTest {
        // given
        coEvery { putChargebackAcceptUseCase(any(), any()) } returns resultNetworkError
        coEvery { userObjUseCase() } returns resultUserObjSuccess

        // when
        viewModel.chargebackAccept(context, EMPTY, validChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.Error)
    }

    @Test
    fun `it should set token error state on MFA token error result of chargebackAccept call`() = runTest {
        // given
        coEvery { putChargebackAcceptUseCase(any(), any()) } returns resultMfaTokenError
        coEvery { userObjUseCase() } returns resultUserObjSuccess

        // when
        viewModel.chargebackAccept(context, EMPTY, validChargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.chargebackAcceptUiState.value is UiAcceptState.ErrorToken)
    }

}

