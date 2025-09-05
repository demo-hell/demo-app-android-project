package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.FlowEmissionHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferScheduledBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferBalanceUiState
import br.com.mobicare.cielo.runTestWithFlowEmission
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixScheduledTransferBalanceViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val requestPixTransferScheduledBalanceUseCase = mockk<RequestPixTransferScheduledBalanceUseCase>()

    private val networkErrorException = CieloAPIException.networkError(EMPTY)
    private val errorResult = CieloDataResult.APIError(networkErrorException)
    private val emptyResult = CieloDataResult.Empty()

    private lateinit var viewModel: PixScheduledTransferBalanceViewModel
    private lateinit var emissionHandler: FlowEmissionHandler<PixScheduledTransferBalanceUiState>

    @Before
    fun setUp() {
        viewModel =
            PixScheduledTransferBalanceViewModel(
                getUserObjUseCase,
                requestPixTransferScheduledBalanceUseCase,
            )
        emissionHandler = FlowEmissionHandler(viewModel.scheduledBalanceState)

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    // ========================
    // requestTransfer
    // ========================

    @Test
    fun `it should set PixScheduledTransferBalanceUiState_Idle as initial state`() =
        runTestWithFlowEmission(emissionHandler) { emissions ->
            assertThat(emissions[0])
                .isInstanceOf(PixScheduledTransferBalanceUiState.Idle.javaClass)
        }

    @Test
    fun `it should set PixScheduledTransferBalanceUiState_Success on requestTransfer call`() =
        runTestWithFlowEmission(emissionHandler) { emissions ->
            // given
            coEvery { requestPixTransferScheduledBalanceUseCase(any()) } returns CieloDataResult.Success(Unit)

            // when
            viewModel.requestTransfer(EMPTY)

            // then
            assertThat(emissions[1])
                .isInstanceOf(PixScheduledTransferBalanceUiState.Success.javaClass)
        }

    @Test
    fun `it should set PixScheduledTransferBalanceUiState_InsufficientBalanceError on requestTransfer call when result is error`() =
        runTestWithFlowEmission(emissionHandler) { emissions ->
            // given
            val insufficientBalanceErrorResult =
                CieloDataResult.APIError(
                    CieloAPIException.networkError(
                        message = EMPTY,
                        newErrorMessage = NewErrorMessage(flagErrorCode = "INSUFFICIENT_BALANCE"),
                    ),
                )
            coEvery { requestPixTransferScheduledBalanceUseCase(any()) } returns insufficientBalanceErrorResult

            // when
            viewModel.requestTransfer(EMPTY)

            // then
            assertThat(emissions[1])
                .isInstanceOf(PixScheduledTransferBalanceUiState.InsufficientBalanceError.javaClass)
        }

    @Test
    fun `it should set PixScheduledTransferBalanceUiState_GenericError on requestTransfer call when result is error`() =
        runTestWithFlowEmission(emissionHandler) { emissions ->
            // given
            coEvery { requestPixTransferScheduledBalanceUseCase(any()) } returns errorResult

            // when
            viewModel.requestTransfer(EMPTY)

            // then
            assertThat(emissions[1])
                .isInstanceOf(PixScheduledTransferBalanceUiState.GenericError.javaClass)
        }

    @Test
    fun `it should set PixScheduledTransferBalanceUiState_GenericError on requestTransfer call when result is empty`() =
        runTestWithFlowEmission(emissionHandler) { emissions ->
            // given
            coEvery { requestPixTransferScheduledBalanceUseCase(any()) } returns emptyResult

            // when
            viewModel.requestTransfer(EMPTY)

            // then
            assertThat(emissions[1])
                .isInstanceOf(PixScheduledTransferBalanceUiState.GenericError.javaClass)
        }
}
