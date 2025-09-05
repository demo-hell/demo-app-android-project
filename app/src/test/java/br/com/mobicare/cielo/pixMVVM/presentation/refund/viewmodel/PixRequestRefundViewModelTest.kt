package br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundReceiptsUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundReceiptsSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundReceiptsUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixAccountBalanceFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixRequestRefundViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val transferDetail = PixTransactionsFactory.TransferDetail.entity
    private val refundReceipts = PixRefundsFactory.RefundReceipts.entity
    private val accountBalance = PixAccountBalanceFactory.pixAccountBalanceEntity

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixRefundReceiptsUseCase = mockk<GetPixRefundReceiptsUseCase>()
    private val getPixAccountBalanceUseCase = mockk<GetPixAccountBalanceUseCase>()
    private val refundReceiptsSuccessStateHandler = mockk<PixRefundReceiptsSuccessStateHandler>()

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private lateinit var viewModel: PixRequestRefundViewModel
    private lateinit var refundReceiptsStates: List<PixRefundReceiptsUiState?>

    @Before
    fun setUp() {
        viewModel = PixRequestRefundViewModel(
            getUserObjUseCase,
            getPixRefundReceiptsUseCase,
            getPixAccountBalanceUseCase,
            refundReceiptsSuccessStateHandler
        )
        refundReceiptsStates = viewModel.refundReceiptsUiState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    // ========================
    // getRefundReceipts
    // ========================

    @Test
    fun `it should set PixRefundReceiptsUiState_Success on getRefundReceipts call`() = runTest {
        // given
        coEvery { getPixAccountBalanceUseCase() } returns CieloDataResult.Success(accountBalance)
        coEvery { getPixRefundReceiptsUseCase(any()) } returns CieloDataResult.Success(refundReceipts)
        coEvery { refundReceiptsSuccessStateHandler(any(), any()) } returns PixRefundReceiptsUiState.FullyRefunded

        // when
        viewModel.getRefundReceipts(transferDetail)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundReceiptsStates[0]).isInstanceOf(PixRefundReceiptsUiState.Loading::class.java)
        assertThat(refundReceiptsStates[1]).isInstanceOf(PixRefundReceiptsUiState.Success::class.java)

        assertThat(viewModel.transferDetail).isEqualTo(transferDetail)
        assertThat(viewModel.refundReceipts).isEqualTo(refundReceipts)
        assertThat(viewModel.currentBalance).isEqualTo(accountBalance.currentBalance)
    }

    @Test
    fun `it should set PixRefundReceiptsUiState_ErrorWithExpiredRefund on getRefundReceipts`() = runTest {
        // given
        val transferDetailWithExpiredReversal = transferDetail.copy(
            expiredReversal = true
        )
        coEvery { getPixAccountBalanceUseCase() } returns CieloDataResult.Success(accountBalance)
        coEvery { getPixRefundReceiptsUseCase(any()) } returns errorResult

        // when
        viewModel.getRefundReceipts(transferDetailWithExpiredReversal)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundReceiptsStates[0]).isInstanceOf(PixRefundReceiptsUiState.Loading::class.java)
        assertThat(refundReceiptsStates[1]).isInstanceOf(PixRefundReceiptsUiState.ErrorWithExpiredRefund::class.java)
    }

    @Test
    fun `it should set PixRefundReceiptsUiState_ErrorWithNotExpiredRefund on getRefundReceipts`() = runTest {
        // given
        val transferDetailWithNotExpiredReversal = transferDetail.copy(
            expiredReversal = false
        )
        coEvery { getPixAccountBalanceUseCase() } returns CieloDataResult.Success(accountBalance)
        coEvery { getPixRefundReceiptsUseCase(any()) } returns errorResult

        // when
        viewModel.getRefundReceipts(transferDetailWithNotExpiredReversal)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(refundReceiptsStates[0]).isInstanceOf(PixRefundReceiptsUiState.Loading::class.java)
        assertThat(refundReceiptsStates[1]).isInstanceOf(PixRefundReceiptsUiState.ErrorWithNotExpiredRefund::class.java)
    }

}

