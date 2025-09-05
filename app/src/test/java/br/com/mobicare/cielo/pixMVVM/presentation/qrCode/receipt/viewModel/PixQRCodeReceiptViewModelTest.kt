package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.receipt.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferScheduleDetailUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixReceiptQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixQRCodeReceiptViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixTransferDetailsUseCase = mockk<GetPixTransferDetailsUseCase>()
    private val getPixTransferScheduleDetailUseCase = mockk<GetPixTransferScheduleDetailUseCase>()

    private val context = mockk<Context>()
    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    private val transferResultForTransferDetail =
        PixTransferResult(
            endToEndId = "endToEndId",
            transactionCode = "transactionCode",
        )

    private val transferResultForScheduleDetail = PixTransferResult(schedulingCode = "schedulingCode")

    private val networkError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val logoutNeedError =
        CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
                newErrorMessage = NewErrorMessage(actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR),
            ),
        )

    private lateinit var viewModel: PixQRCodeReceiptViewModel

    @Before
    fun setup() {
        viewModel =
            PixQRCodeReceiptViewModel(
                getUserObjUseCase,
                getPixTransferDetailsUseCase,
                getPixTransferScheduleDetailUseCase,
            )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set uiState with TransactionExecutedSuccess when getPixTransferDetailsUseCase return success`() =
        runBlocking {
            coEvery {
                getPixTransferDetailsUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.TransferDetail.entity)

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForTransferDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.TransactionExecutedSuccess)
        }

    @Test
    fun `it should set uiState with Error when getPixTransferDetailsUseCase return empty`() =
        runBlocking {
            coEvery {
                getPixTransferDetailsUseCase.invoke(any())
            } returns CieloDataResult.Empty()

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForTransferDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.Error)
        }

    @Test
    fun `it should set uiState with Error when getPixTransferDetailsUseCase return network error`() =
        runBlocking {
            coEvery {
                getPixTransferDetailsUseCase.invoke(any())
            } returns networkError

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForTransferDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.Error)
        }

    @Test
    fun `it should set uiState with ReturnBackScreen when getPixTransferDetailsUseCase return logout need error`() =
        runBlocking {
            coEvery {
                getPixTransferDetailsUseCase.invoke(any())
            } returns logoutNeedError

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForTransferDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.ReturnBackScreen)
        }

    @Test
    fun `it should set uiState with TransactionScheduledSuccess when getPixTransferScheduleDetailUseCase return success`() =
        runBlocking {
            coEvery {
                getPixTransferScheduleDetailUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.SchedulingDetail.entity)

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForScheduleDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.TransactionScheduledSuccess)
        }

    @Test
    fun `it should set uiState with Error when getPixTransferScheduleDetailUseCase return empty`() =
        runBlocking {
            coEvery {
                getPixTransferScheduleDetailUseCase.invoke(any())
            } returns CieloDataResult.Empty()

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForScheduleDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.Error)
        }

    @Test
    fun `it should set uiState with Error when getPixTransferScheduleDetailUseCase return network error`() =
        runBlocking {
            coEvery {
                getPixTransferScheduleDetailUseCase.invoke(any())
            } returns networkError

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForScheduleDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.HideLoading)
            assert(states[TWO] is PixReceiptQRCodeUIState.Error)
        }

    @Test
    fun `it should set uiState with ReturnBackScreen when getPixTransferScheduleDetailUseCase return logout need error`() =
        runBlocking {
            coEvery {
                getPixTransferScheduleDetailUseCase.invoke(any())
            } returns logoutNeedError

            val states = viewModel.uiState.captureValues()

            viewModel.getTransferOrSchedulingDetails(transferResultForScheduleDetail)

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixReceiptQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixReceiptQRCodeUIState.ReturnBackScreen)
        }
}
