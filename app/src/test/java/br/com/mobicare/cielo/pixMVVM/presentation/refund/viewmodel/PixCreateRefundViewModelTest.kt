package br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixRefundUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailFullUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundDetailSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.models.PixCreateRefundStore
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixCreateRefundUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixCreateRefundViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val refundCreated = PixRefundsFactory.RefundCreated.entity
    private val refundDetailFull = PixRefundsFactory.RefundDetailFull.entity
    private val store = PixCreateRefundStore()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val createPixRefundUseCase = mockk<CreatePixRefundUseCase>()
    private val getPixRefundDetailFullUseCase = mockk<GetPixRefundDetailFullUseCase>()
    private val refundDetailSuccessStateHandler = mockk<PixRefundDetailSuccessStateHandler>()

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val tokenErrorResult = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = Text.OTP)
        )
    )
    private val enhanceErrorResult = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(httpCode = 420)
        )
    )
    private val emptyResult = CieloDataResult.Empty()

    private lateinit var viewModel: PixCreateRefundViewModel
    private lateinit var createRefundStates: List<PixCreateRefundUiState?>
    private lateinit var refundDetailStates: List<PixRefundDetailUiState?>

    @Before
    fun setUp() {
        viewModel = PixCreateRefundViewModel(
            getUserObjUseCase,
            createPixRefundUseCase,
            getPixRefundDetailFullUseCase,
            refundDetailSuccessStateHandler
        )
        createRefundStates = viewModel.createRefundUiState.captureValues()
        refundDetailStates = viewModel.refundDetailUiState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    // ========================
    // createRefund
    // ========================

    @Test
    fun `it should set PixCreateRefundUiState_Success on createRefund call`() = runTest {
        // given
        coEvery { createPixRefundUseCase(any()) } returns CieloDataResult.Success(refundCreated)

        // when
        viewModel.createRefund(store)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(createRefundStates[0]).isInstanceOf(PixCreateRefundUiState.Success::class.java)

        assertThat(viewModel.transactionCode).isEqualTo(refundCreated.transactionCode)
    }

    @Test
    fun `it should set PixCreateRefundUiState_TokenError on createRefund`() = runTest {
        // given
        coEvery { createPixRefundUseCase(any()) } returns tokenErrorResult

        // when
        viewModel.createRefund(store)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(createRefundStates[0]).isInstanceOf(PixCreateRefundUiState.TokenError::class.java)
    }

    @Test
    fun `it should set PixCreateRefundUiState_Unprocessable on createRefund`() = runTest {
        // given
        coEvery { createPixRefundUseCase(any()) } returns enhanceErrorResult

        // when
        viewModel.createRefund(store)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(createRefundStates[0]).isInstanceOf(PixCreateRefundUiState.Unprocessable::class.java)
    }

    @Test
    fun `it should set PixCreateRefundUiState_GenericError on createRefund`() = runTest {
        // given
        coEvery { createPixRefundUseCase(any()) } returns errorResult

        // when
        viewModel.createRefund(store)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(createRefundStates[0]).isInstanceOf(PixCreateRefundUiState.GenericError::class.java)
    }

    @Test
    fun `it should set PixCreateRefundUiState_GenericError on createRefund when result is empty`() = runTest {
        // given
        coEvery { createPixRefundUseCase(any()) } returns emptyResult

        // when
        viewModel.createRefund(store)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(createRefundStates[0]).isInstanceOf(PixCreateRefundUiState.GenericError::class.java)
    }

    // ========================
    // getRefundDetail
    // ========================

    @Test
    fun `it should set PixRefundDetailUiState_StatusExecuted on getRefundDetailFull call`() = runTest {
        // given
        coEvery { getPixRefundDetailFullUseCase(any()) } returns CieloDataResult.Success(refundDetailFull)
        coEvery { refundDetailSuccessStateHandler(any()) } returns PixRefundDetailUiState.StatusExecuted

        // when
        viewModel.getRefundDetail()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundDetailStates[0]).isInstanceOf(PixRefundDetailUiState.Loading::class.java)
        assertThat(refundDetailStates[1]).isInstanceOf(PixRefundDetailUiState.StatusExecuted::class.java)
        assertThat(viewModel.refundDetailFull).isEqualTo(refundDetailFull)
    }

    @Test
    fun `it should set PixRefundDetailUiState_StatusPending on getRefundDetailFull call`() = runTest {
        // given
        coEvery { getPixRefundDetailFullUseCase(any()) } returns CieloDataResult.Success(refundDetailFull)
        coEvery { refundDetailSuccessStateHandler(any()) } returns PixRefundDetailUiState.StatusPending

        // when
        viewModel.getRefundDetail()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundDetailStates[0]).isInstanceOf(PixRefundDetailUiState.Loading::class.java)
        assertThat(refundDetailStates[1]).isInstanceOf(PixRefundDetailUiState.StatusPending::class.java)
        assertThat(viewModel.refundDetailFull).isEqualTo(refundDetailFull)
    }

    @Test
    fun `it should set PixRefundDetailUiState_StatusNotExecuted on getRefundDetailFull call`() = runTest {
        // given
        coEvery { getPixRefundDetailFullUseCase(any()) } returns CieloDataResult.Success(refundDetailFull)
        coEvery { refundDetailSuccessStateHandler(any()) } returns PixRefundDetailUiState.StatusNotExecuted

        // when
        viewModel.getRefundDetail()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundDetailStates[0]).isInstanceOf(PixRefundDetailUiState.Loading::class.java)
        assertThat(refundDetailStates[1]).isInstanceOf(PixRefundDetailUiState.StatusNotExecuted::class.java)
        assertThat(viewModel.refundDetailFull).isEqualTo(refundDetailFull)
    }

    @Test
    fun `it should set PixRefundDetailUiState_Error on getRefundDetailFull call`() = runTest {
        // given
        coEvery { getPixRefundDetailFullUseCase(any()) } returns errorResult

        // when
        viewModel.getRefundDetail()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundDetailStates[0]).isInstanceOf(PixRefundDetailUiState.Loading::class.java)
        assertThat(refundDetailStates[1]).isInstanceOf(PixRefundDetailUiState.Error::class.java)
    }

    @Test
    fun `it should set PixRefundDetailUiState_Error on getRefundDetailFull call when result is empty`() = runTest {
        // given
        coEvery { getPixRefundDetailFullUseCase(any()) } returns emptyResult

        // when
        viewModel.getRefundDetail()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundDetailStates[0]).isInstanceOf(PixRefundDetailUiState.Loading::class.java)
        assertThat(refundDetailStates[1]).isInstanceOf(PixRefundDetailUiState.Error::class.java)
    }

}

