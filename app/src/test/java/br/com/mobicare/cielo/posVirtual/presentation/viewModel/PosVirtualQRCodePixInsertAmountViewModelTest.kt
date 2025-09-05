package br.com.mobicare.cielo.posVirtual.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateQRCodePixUseCase
import br.com.mobicare.cielo.posVirtual.presentation.qrCodePix.insertAmount.PosVirtualQRCodePixInsertAmountViewModel
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INTEGRATION_ERROR
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INVALID_AMOUNT
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_LIMIT_EXCEEDED
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_TIME_OUT
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualQRCodePixState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualQRCodePixInsertAmountViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val postPosVirtualCreateQRCodePixUseCase = mockk<PostPosVirtualCreateQRCodePixUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PosVirtualQRCodePixInsertAmountViewModel

    private val posVirtualCreateQRCodeResponse = PosVirtualFactory.posVirtualCreateQRCodeResponse
    private val resultPosVirtualCreateQRCodeSuccess = CieloDataResult.Success(posVirtualCreateQRCodeResponse)
    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultEmpty = CieloDataResult.Empty()
    private val resultGenericError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultTimeOutError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_TIME_OUT)
        )
    )
    private val resultInvalidAmountError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_INVALID_AMOUNT)
        )
    )
    private val resultIntegrationError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_INTEGRATION_ERROR)
        )
    )
    private val resultLimitExceededError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_LIMIT_EXCEEDED)
        )
    )
    private val resultMfaTokenError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
            newErrorMessage = NewErrorMessage(flagErrorCode = OTP)
        )
    )

    @Before
    fun setup() {
        viewModel = PosVirtualQRCodePixInsertAmountViewModel(
            postPosVirtualCreateQRCodePixUseCase,
            getUserObjUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as Success when create qr code with success`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultPosVirtualCreateQRCodeSuccess

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.Success)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as ErrorGeneric when you give empty in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultEmpty

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.GenericError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as GenericError when you give generic error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultGenericError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.GenericError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as TimeOutError when you give time out error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultTimeOutError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.TimeOutError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as InvalidAmountError when you give invalid amount error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultInvalidAmountError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.InvalidAmountError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as IntegrationError when you give integration error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultIntegrationError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.IntegrationError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as LimitExceededError when you give limit exceeded error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultLimitExceededError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.LimitExceededError)
    }

    @Test
    fun `it should set UIPosVirtualQRCodePixState as ErrorToken when you give token error in the request to create QR Code`() = runTest {
        coEvery {
            postPosVirtualCreateQRCodePixUseCase(
                any(),
                any()
            )
        } returns resultMfaTokenError

        viewModel.generateQRCode(context, DEFAULT_OTP, PosVirtualFactory.amount, PosVirtualFactory.logicalNumber)

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiPosVirtualQRCodePixStateLiveData.value is UIPosVirtualQRCodePixState.TokenError)
    }

}