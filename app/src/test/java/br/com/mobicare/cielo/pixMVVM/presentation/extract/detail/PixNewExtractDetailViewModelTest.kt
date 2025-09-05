package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_403
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixEnable
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CancelPixTransferScheduleUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailFullUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundReceiptsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferScheduleDetailUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundReceiptsResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixScheduleResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixTransferResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundReceiptsUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixRefundUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixScheduleUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailCancelScheduleUIState
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.state.PixExtractDetailUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixRefundsFactory
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixNewExtractDetailViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixRefundReceiptsUseCase = mockk<GetPixRefundReceiptsUseCase>()
    private val getPixRefundDetailUseCase = mockk<GetPixRefundDetailFullUseCase>()
    private val getPixTransferDetailsUseCase = mockk<GetPixTransferDetailsUseCase>()
    private val getPixTransferScheduleDetailUseCase = mockk<GetPixTransferScheduleDetailUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()
    private val cancelPixTransferScheduleUseCase = mockk<CancelPixTransferScheduleUseCase>()
    private val transferResultHandler = mockk<PixTransferResultHandler>()
    private val refundResultHandler = mockk<PixRefundResultHandler>()
    private val scheduleResultHandler = mockk<PixScheduleResultHandler>()
    private val refundReceiptsResultHandler = mockk<PixRefundReceiptsResultHandler>()

    private val params = PixTransactionsFactory.MockedParams
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()
    private val mfaTokenErrorResult =
        CieloDataResult.APIError(
            CieloAPIException(
                httpStatusCode = HTTP_STATUS_403,
                actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
                newErrorMessage = NewErrorMessage(flagErrorCode = Text.OTP, actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION),
            ),
        )

    private lateinit var transferUiState: List<PixExtractDetailUiState<PixTransferUiResult>?>
    private lateinit var refundUiState: List<PixExtractDetailUiState<PixRefundUiResult>?>
    private lateinit var scheduleUiState: List<PixExtractDetailUiState<PixScheduleUiResult>?>
    private lateinit var cancelScheduleUiState: List<PixExtractDetailCancelScheduleUIState?>
    private lateinit var refundReceiptsUiState: List<PixExtractDetailUiState<PixRefundReceiptsUiResult>?>

    private lateinit var viewModel: PixNewExtractDetailViewModel

    @Before
    fun setUp() {
        viewModel =
            PixNewExtractDetailViewModel(
                getUserObjUseCase,
                getPixRefundReceiptsUseCase,
                getPixRefundDetailUseCase,
                getPixTransferDetailsUseCase,
                getPixTransferScheduleDetailUseCase,
                getFeatureTogglePreferenceUseCase,
                cancelPixTransferScheduleUseCase,
                transferResultHandler,
                refundResultHandler,
                scheduleResultHandler,
                refundReceiptsResultHandler,
            )

        transferUiState = viewModel.transferState.captureValues()
        refundUiState = viewModel.refundState.captureValues()
        scheduleUiState = viewModel.scheduleState.captureValues()
        cancelScheduleUiState = viewModel.cancelScheduleState.captureValues()
        refundReceiptsUiState = viewModel.refundReceiptsState.captureValues()

        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns CieloDataResult.Success(true)
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    private fun <T> assertLoadingState(state: PixExtractDetailUiState<T>?) {
        assertThat(state).isInstanceOf(PixExtractDetailUiState.Loading::class.java)
    }

    // ============================
    // TRANSFER DETAILS
    // ============================

    private fun <K> assertTransferSuccessState(resultClass: Class<K>) {
        assertThat(transferUiState[1]).isInstanceOf(PixExtractDetailUiState.Success::class.java)
        assertThat((transferUiState[1] as PixExtractDetailUiState.Success).result).isInstanceOf(
            resultClass,
        )
    }

    private fun <K> mockAndRunPixTransferAssertion(
        result: PixTransferUiResult,
        klass: Class<K>,
    ) {
        // given
        val expectedEntity = PixTransactionsFactory.TransferDetail.entity

        coEvery { getPixTransferDetailsUseCase(any()) } returns
            CieloDataResult.Success(
                expectedEntity,
            )
        coEvery { transferResultHandler(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(transferUiState[0])
        assertTransferSuccessState(klass)
        assertEquals(expectedEntity, viewModel.transferDetail)
    }

    @Test
    fun `it should assert a PixTransferUiResult_TransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.TransferSent(),
                PixTransferUiResult.TransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeTransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeTransferSent(),
                PixTransferUiResult.QrCodeTransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeChangeTransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeChangeTransferSent,
                PixTransferUiResult.QrCodeChangeTransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeWithdrawalTransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeWithdrawalTransferSent,
                PixTransferUiResult.QrCodeWithdrawalTransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_AutomaticTransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.AutomaticTransferSent,
                PixTransferUiResult.AutomaticTransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_FeeTransferSent result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.FeeTransferSent,
                PixTransferUiResult.FeeTransferSent::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_TransferReceived result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.TransferReceived(),
                PixTransferUiResult.TransferReceived::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeTransferReceived result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeTransferReceived(),
                PixTransferUiResult.QrCodeTransferReceived::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeChangeTransferReceived result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeChangeTransferReceived,
                PixTransferUiResult.QrCodeChangeTransferReceived::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_QrCodeWithdrawalTransferReceived result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.QrCodeWithdrawalTransferReceived,
                PixTransferUiResult.QrCodeWithdrawalTransferReceived::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_TransferInProcess result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.TransferInProcess(),
                PixTransferUiResult.TransferInProcess::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_AutomaticTransferInProcess result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.AutomaticTransferInProcess,
                PixTransferUiResult.AutomaticTransferInProcess::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_FeeTransferInProcess result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.FeeTransferInProcess,
                PixTransferUiResult.FeeTransferInProcess::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_TransferCanceled result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.TransferCanceled(),
                PixTransferUiResult.TransferCanceled::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_AutomaticTransferCanceled result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.AutomaticTransferCanceled,
                PixTransferUiResult.AutomaticTransferCanceled::class.java,
            )
        }

    @Test
    fun `it should assert a PixTransferUiResult_FeeTransferCanceled result with correct data`() =
        runTest {
            mockAndRunPixTransferAssertion(
                PixTransferUiResult.FeeTransferCanceled,
                PixTransferUiResult.FeeTransferCanceled::class.java,
            )
        }

    private fun mockAndRunTransferErrorAssertion(result: CieloDataResult<PixTransferDetail>) {
        // given
        coEvery { getPixTransferDetailsUseCase(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(transferUiState[0])
        assertThat(transferUiState[1]).isInstanceOf(PixExtractDetailUiState.Error::class.java)
    }

    @Test
    fun `it should set error state when result is either empty or error on getTransferDetails call`() =
        runTest {
            mockAndRunTransferErrorAssertion(errorResult)
            mockAndRunTransferErrorAssertion(emptyResult)
        }

    // ============================
    // REFUND RECEIPTS
    // ============================

    private fun <K> mockAndRunRefundReceiptsAssertion(
        creditTransfer: PixTransferDetail,
        refundReceipts: PixRefundReceipts,
        expectedResult: PixRefundReceiptsUiResult,
        expectedResultClass: Class<K>,
    ): PixRefundReceiptsUiResult {
        // given
        coEvery { getPixTransferDetailsUseCase(any()) } returns
            CieloDataResult.Success(
                creditTransfer,
            )
        coEvery { transferResultHandler(any()) } returns PixTransferUiResult.TransferReceived()
        coEvery { getPixRefundReceiptsUseCase(any()) } returns
            CieloDataResult.Success(
                refundReceipts,
            )
        coEvery { refundReceiptsResultHandler(any(), any()) } returns expectedResult

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(refundReceiptsUiState[0])

        assertThat(refundReceiptsUiState[1])
            .isInstanceOf(PixExtractDetailUiState.Success::class.java)

        val actualResult = (refundReceiptsUiState[1] as PixExtractDetailUiState.Success).result

        assertThat(actualResult).isInstanceOf(expectedResultClass)

        return actualResult
    }

    @Test
    fun `it should assert a PixRefundReceiptsUiResult_CanBeRefunded result with correct data`() =
        runTest {
            val creditTransfer =
                PixTransactionsFactory.TransferDetail.entity.copy(
                    transactionType = PixTransactionType.TRANSFER_CREDIT,
                    transactionStatus = PixTransactionStatus.EXECUTED,
                )

            val expectedEntity = PixRefundsFactory.RefundReceipts.entity
            val expectedResult = PixRefundReceiptsUiResult.CanBeRefunded(expectedEntity)

            val result =
                mockAndRunRefundReceiptsAssertion(
                    creditTransfer = creditTransfer,
                    refundReceipts = expectedEntity,
                    expectedResult = expectedResult,
                    expectedResultClass = PixRefundReceiptsUiResult.CanBeRefunded::class.java,
                )

            val actualResult = (result as PixRefundReceiptsUiResult.CanBeRefunded)

            assertEquals(expectedResult.refundReceipts, actualResult.refundReceipts)
        }

    @Test
    fun `it should assert a PixRefundReceiptsUiResult_CannotBeRefunded result`() =
        runTest {
            val creditTransfer =
                PixTransactionsFactory.TransferDetail.entity.copy(
                    transactionType = PixTransactionType.TRANSFER_CREDIT,
                    transactionStatus = PixTransactionStatus.EXECUTED,
                )

            mockAndRunRefundReceiptsAssertion(
                creditTransfer = creditTransfer,
                refundReceipts = PixRefundsFactory.RefundReceipts.entity,
                expectedResult = PixRefundReceiptsUiResult.CannotBeRefunded,
                expectedResultClass = PixRefundReceiptsUiResult.CannotBeRefunded::class.java,
            )
        }

    private fun mockAndRunRefundReceiptsErrorAssertion(result: CieloDataResult<PixRefundReceipts>) {
        // given
        val creditTransfer =
            PixTransactionsFactory.TransferDetail.entity.copy(
                transactionType = PixTransactionType.TRANSFER_CREDIT,
                transactionStatus = PixTransactionStatus.EXECUTED,
            )

        coEvery { getPixTransferDetailsUseCase(any()) } returns
            CieloDataResult.Success(
                creditTransfer,
            )
        coEvery { transferResultHandler(any()) } returns PixTransferUiResult.TransferReceived()
        coEvery { getPixRefundReceiptsUseCase(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(refundReceiptsUiState[0])
        assertThat(refundReceiptsUiState[1]).isInstanceOf(PixExtractDetailUiState.Error::class.java)
    }

    @Test
    fun `it should set error state when result is either empty or error on getRefundReceipts call`() =
        runTest {
            mockAndRunRefundReceiptsErrorAssertion(errorResult)
            mockAndRunRefundReceiptsErrorAssertion(emptyResult)
        }

    // ============================
    // REFUND DETAILS
    // ============================

    private fun <K> assertRefundSuccessState(resultClass: Class<K>) {
        assertThat(refundUiState[1]).isInstanceOf(PixExtractDetailUiState.Success::class.java)
        assertThat((refundUiState[1] as PixExtractDetailUiState.Success).result).isInstanceOf(
            resultClass,
        )
    }

    private fun <K> mockAndRunPixRefundAssertion(
        result: PixRefundUiResult,
        klass: Class<K>,
    ) {
        // given
        val expectedEntity = PixRefundsFactory.RefundDetailFull.entity

        coEvery { getPixRefundDetailUseCase(any()) } returns CieloDataResult.Success(expectedEntity)
        coEvery { refundResultHandler(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = true,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(refundUiState[0])
        assertRefundSuccessState(klass)
        assertEquals(expectedEntity, viewModel.refundDetailFull)
    }

    @Test
    fun `it should assert a PixRefundUiResult_RefundReceived result with correct data`() =
        runTest {
            mockAndRunPixRefundAssertion(
                PixRefundUiResult.RefundReceived,
                PixRefundUiResult.RefundReceived::class.java,
            )
        }

    @Test
    fun `it should assert a PixRefundUiResult_RefundSentCompleted result with correct data`() =
        runTest {
            mockAndRunPixRefundAssertion(
                PixRefundUiResult.RefundSentCompleted,
                PixRefundUiResult.RefundSentCompleted::class.java,
            )
        }

    @Test
    fun `it should assert a PixRefundUiResult_RefundSentPending result with correct data`() =
        runTest {
            mockAndRunPixRefundAssertion(
                PixRefundUiResult.RefundSentPending,
                PixRefundUiResult.RefundSentPending::class.java,
            )
        }

    @Test
    fun `it should assert a PixRefundUiResult_RefundSentFailed result with correct data`() =
        runTest {
            mockAndRunPixRefundAssertion(
                PixRefundUiResult.RefundSentFailed,
                PixRefundUiResult.RefundSentFailed::class.java,
            )
        }

    private fun mockAndRunRefundErrorAssertion(result: CieloDataResult<PixRefundDetailFull>) {
        // given
        coEvery { getPixRefundDetailUseCase(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = true,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(refundUiState[0])
        assertThat(refundUiState[1]).isInstanceOf(PixExtractDetailUiState.Error::class.java)
    }

    @Test
    fun `it should set error state when result is either empty or error on getRefundDetails call`() =
        runTest {
            mockAndRunRefundErrorAssertion(errorResult)
            mockAndRunRefundErrorAssertion(emptyResult)
        }

    // ============================
    // SCHEDULE DETAILS
    // ============================

    private fun <K> assertScheduleDetailSuccessState(resultClass: Class<K>) {
        assertThat(scheduleUiState[1]).isInstanceOf(PixExtractDetailUiState.Success::class.java)
        assertThat((scheduleUiState[1] as PixExtractDetailUiState.Success).result).isInstanceOf(
            resultClass,
        )
    }

    private fun <K> mockAndRunPixScheduleDetailAssertion(
        result: PixScheduleUiResult,
        klass: Class<K>,
    ) {
        // given
        val expectedEntity = PixTransactionsFactory.SchedulingDetail.entity

        coEvery { getPixTransferScheduleDetailUseCase(any()) } returns
            CieloDataResult.Success(
                expectedEntity,
            )
        coEvery { scheduleResultHandler(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = params.schedulingCode,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(scheduleUiState[0])
        assertScheduleDetailSuccessState(klass)
        assertEquals(expectedEntity, result.data)
    }

    @Test
    fun `it should assert a PixScheduleUiResult_TransferScheduled result with correct data`() =
        runTest {
            mockAndRunPixScheduleDetailAssertion(
                PixScheduleUiResult.TransferScheduled(PixTransactionsFactory.SchedulingDetail.entity),
                PixScheduleUiResult.TransferScheduled::class.java,
            )
        }

    @Test
    fun `it should assert a PixScheduleUiResult_TransferScheduleCanceled result with correct data`() =
        runTest {
            mockAndRunPixScheduleDetailAssertion(
                PixScheduleUiResult.TransferScheduleCanceled(PixTransactionsFactory.SchedulingDetail.entity),
                PixScheduleUiResult.TransferScheduleCanceled::class.java,
            )
        }

    private fun mockAndRunScheduleDetailErrorAssertion(result: CieloDataResult<PixSchedulingDetail>) {
        // given
        coEvery { getPixTransferScheduleDetailUseCase(any()) } returns result

        // when
        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = params.schedulingCode,
            isRefund = false,
        )

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(scheduleUiState[0])
        assertThat(scheduleUiState[1]).isInstanceOf(PixExtractDetailUiState.Error::class.java)
    }

    @Test
    fun `it should set error state when result is either empty or error on getScheduleDetails call`() =
        runTest {
            mockAndRunScheduleDetailErrorAssertion(errorResult)
            mockAndRunScheduleDetailErrorAssertion(emptyResult)
        }

    // ============================
    // CANCEL SCHEDULE
    // ============================

    @Test
    fun `it should set ScheduleDetailSuccess state on cancelTransferSchedule call when transactionStatus equal CANCELLED on schedule details`() =
        runTest {
            coEvery {
                cancelPixTransferScheduleUseCase(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.TransferResult.entity)

            coEvery {
                getPixTransferScheduleDetailUseCase(any())
            } returns
                CieloDataResult.Success(
                    PixTransactionsFactory.SchedulingDetail.entity.copy(
                        status = PixTransactionStatus.CANCELLED,
                    ),
                )

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)
            viewModel.getScheduleDetailsAfterCancelTransferSchedule()

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, FOUR)
            assertThat(cancelScheduleUiState[ZERO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.CancelScheduleSuccess::class.java,
            )
            assertThat(cancelScheduleUiState[ONE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ShowLoading::class.java,
            )
            assertThat(cancelScheduleUiState[TWO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.HideLoading::class.java,
            )
            assertThat(cancelScheduleUiState[THREE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ScheduleDetailSuccess::class.java,
            )
        }

    @Test
    fun `it should set ScheduleDetailPending state on cancelTransferSchedule call when transactionStatus different CANCELLED on schedule details`() =
        runTest {
            coEvery {
                cancelPixTransferScheduleUseCase(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.TransferResult.entity)

            coEvery {
                getPixTransferScheduleDetailUseCase(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.SchedulingDetail.entity)

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)
            viewModel.getScheduleDetailsAfterCancelTransferSchedule()

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, FOUR)
            assertThat(cancelScheduleUiState[ZERO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.CancelScheduleSuccess::class.java,
            )
            assertThat(cancelScheduleUiState[ONE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ShowLoading::class.java,
            )
            assertThat(cancelScheduleUiState[TWO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.HideLoading::class.java,
            )
            assertThat(cancelScheduleUiState[THREE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ScheduleDetailPending::class.java,
            )
        }

    @Test
    fun `it should set ScheduleDetailError state on cancelTransferSchedule call when result is empty or error on getScheduleDetails`() =
        runTest {
            coEvery {
                cancelPixTransferScheduleUseCase(any())
            } returns CieloDataResult.Success(PixTransactionsFactory.TransferResult.entity)

            coEvery {
                getPixTransferScheduleDetailUseCase(any())
            } returns errorResult

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)
            viewModel.getScheduleDetailsAfterCancelTransferSchedule()

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, FOUR)
            assertThat(cancelScheduleUiState[ZERO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.CancelScheduleSuccess::class.java,
            )
            assertThat(cancelScheduleUiState[ONE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ShowLoading::class.java,
            )
            assertThat(cancelScheduleUiState[TWO]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.HideLoading::class.java,
            )
            assertThat(cancelScheduleUiState[THREE]).isInstanceOf(
                PixExtractDetailCancelScheduleUIState.ScheduleDetailError::class.java,
            )
        }

    @Test
    fun `it should set error state when result is error on cancelTransferSchedule call`() =
        runTest {
            coEvery { cancelPixTransferScheduleUseCase(any()) } returns errorResult

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, ONE)
            assertThat(cancelScheduleUiState[ZERO]).isInstanceOf(PixExtractDetailCancelScheduleUIState.CancelScheduleError::class.java)
        }

    @Test
    fun `it should set error state when result is empty on cancelTransferSchedule call`() =
        runTest {
            coEvery { cancelPixTransferScheduleUseCase(any()) } returns emptyResult

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, ONE)
            assertThat(cancelScheduleUiState[ZERO]).isInstanceOf(PixExtractDetailCancelScheduleUIState.CancelScheduleError::class.java)
        }

    @Test
    fun `it should set hideLoadingCancelSchedule state when result error otp, session or role on cancelTransferSchedule call`() =
        runTest {
            coEvery { cancelPixTransferScheduleUseCase(any()) } returns mfaTokenErrorResult

            viewModel.cancelTransferSchedule(otpCode = params.otpCode)

            dispatcherRule.advanceUntilIdle()

            assertEquals(cancelScheduleUiState.size, ONE)
            assertThat(
                cancelScheduleUiState[ZERO],
            ).isInstanceOf(PixExtractDetailCancelScheduleUIState.HideLoadingCancelSchedule::class.java)
        }

    // ============================
    // ENABLE BUTTON
    // ============================

    private fun mockAndRunEnableRefundButtonAssertion(enableRefund: Boolean?): Boolean {
        coEvery { getPixRefundDetailUseCase(any()) } returns
            CieloDataResult.Success(
                PixRefundsFactory.RefundDetailFull.entity.copy(
                    enable = PixEnable(refund = enableRefund),
                ),
            )
        coEvery { refundResultHandler(any()) } returns PixRefundUiResult.RefundReceived

        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = true,
        )

        dispatcherRule.advanceUntilIdle()

        return viewModel.isShowButtonRefund
    }

    @Test
    fun `it should enable refund button`() =
        runTest {
            assertTrue(
                mockAndRunEnableRefundButtonAssertion(enableRefund = true),
            )
        }

    @Test
    fun `it should disable refund button`() =
        runTest {
            assertFalse(
                mockAndRunEnableRefundButtonAssertion(enableRefund = false),
            )
            assertFalse(
                mockAndRunEnableRefundButtonAssertion(enableRefund = null),
            )
        }

    private fun mockAndRunEnableCancelScheduleButtonAssertion(enableCancelSchedule: Boolean?): Boolean {
        val entity =
            PixTransactionsFactory.SchedulingDetail.entity.copy(
                enable = PixEnable(cancelSchedule = enableCancelSchedule),
            )
        coEvery { getPixTransferScheduleDetailUseCase(any()) } returns CieloDataResult.Success(entity)
        coEvery { scheduleResultHandler(any()) } returns PixScheduleUiResult.TransferScheduled(entity)

        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = "123",
            isRefund = false,
        )

        dispatcherRule.advanceUntilIdle()

        return viewModel.isShowButtonCancelSchedule
    }

    @Test
    fun `it should enable cancel schedule button`() =
        runTest {
            assertTrue(
                mockAndRunEnableCancelScheduleButtonAssertion(enableCancelSchedule = true),
            )
        }

    @Test
    fun `it should disable cancel schedule button`() =
        runTest {
            assertFalse(
                mockAndRunEnableCancelScheduleButtonAssertion(enableCancelSchedule = false),
            )
            assertFalse(
                mockAndRunEnableCancelScheduleButtonAssertion(enableCancelSchedule = null),
            )
        }

    private fun mockAndRunEnableRequestAnalysisButtonAssertion(
        enableRequestAnalysis: Boolean?,
        ftRequestAnalysis: Boolean,
    ): Boolean {
        val entity =
            PixTransactionsFactory.TransferDetail.entity.copy(
                enable = PixEnable(requestAnalysis = enableRequestAnalysis),
            )
        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns CieloDataResult.Success(ftRequestAnalysis)
        coEvery { getPixTransferDetailsUseCase(any()) } returns CieloDataResult.Success(entity)
        coEvery { transferResultHandler(any()) } returns PixTransferUiResult.TransferSent()

        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = false,
        )

        dispatcherRule.advanceUntilIdle()

        return viewModel.isShowButtonRequestAnalysis
    }

    @Test
    fun `it should enable request analysis button`() =
        runTest {
            assertTrue(
                mockAndRunEnableRequestAnalysisButtonAssertion(
                    enableRequestAnalysis = true,
                    ftRequestAnalysis = true,
                ),
            )
        }

    @Test
    fun `it should disable request analysis button`() =
        runTest {
            assertFalse(
                mockAndRunEnableRequestAnalysisButtonAssertion(
                    enableRequestAnalysis = true,
                    ftRequestAnalysis = false,
                ),
            )
            assertFalse(
                mockAndRunEnableRequestAnalysisButtonAssertion(
                    enableRequestAnalysis = false,
                    ftRequestAnalysis = true,
                ),
            )
            assertFalse(
                mockAndRunEnableRequestAnalysisButtonAssertion(
                    enableRequestAnalysis = null,
                    ftRequestAnalysis = true,
                ),
            )
        }

    private fun mockAndRunEnableAccessOriginalTransactionButtonAssertion(idEndToEndOriginal: String?): Boolean {
        val entity = PixRefundsFactory.RefundDetailFull.entity
        val result =
            entity.copy(
                refundDetail =
                    entity.refundDetail?.copy(
                        idEndToEndOriginal = idEndToEndOriginal,
                    ),
            )
        coEvery { getPixRefundDetailUseCase(any()) } returns CieloDataResult.Success(result)
        coEvery { refundResultHandler(any()) } returns PixRefundUiResult.RefundReceived

        viewModel.start(
            transactionCode = params.transactionCode,
            endToEndId = params.endToEndId,
            schedulingCode = null,
            isRefund = true,
        )

        dispatcherRule.advanceUntilIdle()

        return viewModel.isShowButtonAccessOriginalTransaction
    }

    @Test
    fun `it should enable access original transaction button`() =
        runTest {
            assertTrue(
                mockAndRunEnableAccessOriginalTransactionButtonAssertion(
                    idEndToEndOriginal = "abc123",
                ),
            )
        }

    @Test
    fun `it should disable access original transaction button`() =
        runTest {
            assertFalse(
                mockAndRunEnableAccessOriginalTransactionButtonAssertion(
                    idEndToEndOriginal = null,
                ),
            )
        }
}
