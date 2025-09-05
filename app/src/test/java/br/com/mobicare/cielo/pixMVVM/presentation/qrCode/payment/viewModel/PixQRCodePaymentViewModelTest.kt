package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.commons.utils.getTotalValueChange
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferWithKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixPaymentQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class PixQRCodePaymentViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val requestPixTransferWithKeyUseCase = mockk<RequestPixTransferWithKeyUseCase>()

    private val context = mockk<Context>()

    private lateinit var viewModel: PixQRCodePaymentViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    @Before
    fun setup() {
        viewModel =
            PixQRCodePaymentViewModel(
                getUserObjUseCase,
                requestPixTransferWithKeyUseCase,
            )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `set pix decode qrcode with change amount is null`() {
        viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)

        assertEquals(PixQRCodeFactory.pixDecodeQRCode, viewModel.pixDecodeQRCode.value)
        assertEquals(PixQRCodeFactory.pixDecodeQRCode.finalAmount, viewModel.paymentAmount.value)
        assertEquals(PixQRCodeFactory.pixDecodeQRCode.finalAmount, viewModel.finalAmount.value)
        assertNull(viewModel.changeAmount.value)
    }

    @Test
    fun `set pix decode qrcode with change amount is not null`() {
        val changeAmount = 10.0
        val decode = PixQRCodeFactory.pixDecodeQRCode.copy(changeAmount = changeAmount, pixType = PixQrCodeOperationType.CHANGE)
        val finalAmount = getTotalValueChange(decode.originalAmount, decode.changeAmount)

        viewModel.setPixDecodeQRCode(decode)

        assertEquals(decode, viewModel.pixDecodeQRCode.value)
        assertEquals(PixQRCodeFactory.pixDecodeQRCode.originalAmount, viewModel.paymentAmount.value)
        assertEquals(finalAmount, viewModel.finalAmount.value)
        assertEquals(decode.changeAmount, viewModel.changeAmount.value)
    }

    @Test
    fun `set payment date updates _paymentDate`() {
        val expectedDate = Calendar.getInstance().apply { set(2024, 1, 1) }

        viewModel.setPaymentDate(expectedDate)

        assertEquals(expectedDate, viewModel.paymentDate.value)
    }

    @Test
    fun `set optional message update _optionalMessage`() {
        val expectedOptionalMessage = "Mensagem opcional de teste"

        viewModel.setOptionalMessage(expectedOptionalMessage)

        assertEquals(expectedOptionalMessage, viewModel.optionalMessage.value)
    }

    @Test
    fun `set fingerprint updates fingerprint`() {
        val field: Field = PixQRCodePaymentViewModel::class.java.getDeclaredField("fingerprint")
        field.isAccessible = true
        val expectedFingerprint = "fingerprint"

        viewModel.setFingerprint(expectedFingerprint)

        assertEquals(expectedFingerprint, field.get(viewModel) as String)
    }

    @Test
    fun `it should set uiState with TransactionExecuted when to pay QR Code`() =
        runBlocking {
            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransferResult(transactionStatus = PixTransactionStatus.EXECUTED))

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.TransactionExecuted)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with TransactionScheduled when to pay QR Code`() =
        runBlocking {
            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransferResult(transactionStatus = PixTransactionStatus.SCHEDULED))

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.TransactionScheduled)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with TransactionProcessing when to pay QR Code`() =
        runBlocking {
            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransferResult(transactionStatus = PixTransactionStatus.PROCESSING))

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.TransactionProcessing)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with TransactionFailed when to pay QR Code`() =
        runBlocking {
            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns CieloDataResult.Success(PixTransferResult(transactionStatus = PixTransactionStatus.FAILED))

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.TransactionFailed)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with GenericError when to pay QR Code and requestPixTransferWithKeyUseCase return NETWORK_ERROR`() =
        runBlocking {
            val networkError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns networkError

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.GenericError)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)

            assertEquals(networkError.apiException.newErrorMessage, (states[ZERO] as PixPaymentQRCodeUIState.GenericError).error)
        }

    @Test
    fun `it should set uiState with FourHundredError when to pay QR Code and requestPixTransferWithKeyUseCase return HTTP_UNKNOWN`() =
        runBlocking {
            val error422 =
                CieloDataResult.APIError(
                    CieloAPIException(
                        httpStatusCode = HTTP_UNKNOWN,
                        actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                        newErrorMessage = NewErrorMessage(httpCode = HTTP_UNKNOWN),
                    ),
                )

            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns error422

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.FourHundredError)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)

            assertEquals(error422.apiException.newErrorMessage, (states[ZERO] as PixPaymentQRCodeUIState.FourHundredError).error)
        }

    @Test
    fun `it should set uiState with HideLoading when to pay QR Code and requestPixTransferWithKeyUseCase return LOGOUT_NEEDED_ERROR`() =
        runBlocking {
            val logoutNeedError =
                CieloDataResult.APIError(
                    CieloAPIException(
                        actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
                        newErrorMessage = NewErrorMessage(actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR),
                    ),
                )

            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns logoutNeedError

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.HideLoading)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with GenericError when to pay QR Code and requestPixTransferWithKeyUseCase return EMPTY error`() =
        runBlocking {
            coEvery {
                requestPixTransferWithKeyUseCase.invoke(any())
            } returns CieloDataResult.Empty()

            val states = viewModel.uiState.captureValues()

            viewModel.setPixDecodeQRCode(PixQRCodeFactory.pixDecodeQRCode)
            viewModel.toPay("666666")

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixPaymentQRCodeUIState.GenericError)
            assert(states[ONE] is PixPaymentQRCodeUIState.DoNothing)

            assertNull((states[ZERO] as PixPaymentQRCodeUIState.GenericError).error)
        }
}
