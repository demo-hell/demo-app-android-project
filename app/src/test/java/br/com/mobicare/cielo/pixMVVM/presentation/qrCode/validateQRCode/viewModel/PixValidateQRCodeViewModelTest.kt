package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.FOUR
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
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixAllowsChangeValueEnum
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixDecodeQRCodeUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.enums.PixQRCodeScreenEnum
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixDecodeQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants
import br.com.mobicare.cielo.pixMVVM.utils.PixQRCodeFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixValidateQRCodeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postPixDecodeQRCodeUseCase = mockk<PostPixDecodeQRCodeUseCase>()

    private val context = mockk<Context>()

    private lateinit var viewModel: PixValidateQRCodeViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())

    @Before
    fun setup() {
        viewModel =
            PixValidateQRCodeViewModel(
                getUserObjUseCase,
                postPixDecodeQRCodeUseCase,
            )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `getScreenOriginDecode returns correct screen origin`() {
        val expectedScreenOrigin = PixQRCodeScreenEnum.DECODE
        viewModel.setScreenOriginDecode(expectedScreenOrigin)

        dispatcherRule.advanceUntilIdle()

        val actualScreenOrigin = viewModel.getScreenOriginDecode()

        assertEquals(expectedScreenOrigin, actualScreenOrigin)
    }

    @Test
    fun `setScreenOriginDecode updates screen origin correctly`() {
        val newScreenOrigin = PixQRCodeScreenEnum.COPY_PASTE

        viewModel.setScreenOriginDecode(newScreenOrigin)

        dispatcherRule.advanceUntilIdle()

        assertEquals(newScreenOrigin, viewModel.getScreenOriginDecode())
    }

    @Test
    fun `set qrCode updates qrCode LiveData`() {
        val values = viewModel.qrCode.captureValues()

        viewModel.setQRCode(PixQRCodeFactory.qrCode)

        dispatcherRule.advanceUntilIdle()

        assertEquals(ONE, values.size)
        assertEquals(PixQRCodeFactory.qrCode, values[ZERO])
    }

    @Test
    fun `it should set uiState with NavigateToPixQRCodePaymentSummary when validate QR Code with QR Code is valid and amount is not zero`() =
        runTest {
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns CieloDataResult.Success(PixQRCodeFactory.pixDecodeQRCode.copy(finalAmount = 200.00))

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentSummary)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with NavigateToPixQRCodePaymentInsertValue when validate QR Code with QR Code is valid and amount is zero`() =
        runTest {
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns CieloDataResult.Success(PixQRCodeFactory.pixDecodeQRCode)

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentInsertAmount)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with GenericError when validate QR Code with QR Code is valid and amount is zero and is not allowed change value`() =
        runTest {
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns
                CieloDataResult.Success(
                    PixQRCodeFactory.pixDecodeQRCode.copy(modalityAlteration = PixAllowsChangeValueEnum.NOT_ALLOWED),
                )

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.GenericError)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)

            assertEquals(null, (states[TWO] as PixDecodeQRCodeUIState.GenericError).error)
        }

    @Test
    fun `it should set uiState with GenericError when validate QR Code with QR Code equal to ERROR_READ_QR_CODE`() =
        runTest {
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns
                CieloDataResult.Success(
                    PixQRCodeFactory.pixDecodeQRCode.copy(modalityAlteration = PixAllowsChangeValueEnum.NOT_ALLOWED),
                )

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixConstants.ERROR_READ_QR_CODE)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(TWO, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.GenericError)
            assert(states[ONE] is PixDecodeQRCodeUIState.DoNothing)

            assertEquals(null, (states[ZERO] as PixDecodeQRCodeUIState.GenericError).error)
        }

    @Test
    fun `it should set uiState with GenericError when validate QR Code and postDecodeQRCodeUseCase return NETWORK_ERROR`() =
        runTest {
            val networkError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns networkError

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.GenericError)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)

            assertEquals(networkError.apiException.newErrorMessage, (states[TWO] as PixDecodeQRCodeUIState.GenericError).error)
        }

    @Test
    fun `it should set uiState with CloseActivity when validate QR Code and postDecodeQRCodeUseCase return LOGOUT_NEEDED_ERROR`() =
        runTest {
            val logoutNeedError =
                CieloDataResult.APIError(
                    CieloAPIException(
                        actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
                        newErrorMessage = NewErrorMessage(actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR),
                    ),
                )

            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns logoutNeedError

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.CloseActivity)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)
        }

    @Test
    fun `it should set uiState with GenericError when validate QR Code and postDecodeQRCodeUseCase return empty error`() =
        runTest {
            coEvery {
                postPixDecodeQRCodeUseCase.invoke(any())
            } returns CieloDataResult.Empty()

            val states = viewModel.uiState.captureValues()

            viewModel.setQRCode(PixQRCodeFactory.qrCode)
            viewModel.validateQRCode()

            dispatcherRule.advanceUntilIdle()

            assertEquals(FOUR, states.size)

            assert(states[ZERO] is PixDecodeQRCodeUIState.ShowLoading)
            assert(states[ONE] is PixDecodeQRCodeUIState.HideLoading)
            assert(states[TWO] is PixDecodeQRCodeUIState.GenericError)
            assert(states[THREE] is PixDecodeQRCodeUIState.DoNothing)

            assertEquals(null, (states[TWO] as PixDecodeQRCodeUIState.GenericError).error)
        }
}
