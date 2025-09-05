package br.com.mobicare.cielo.pixMVVM.presentation.transfer

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.dateUtils.plusDays
import br.com.cielo.libflue.util.dateUtils.plusMonths
import br.com.cielo.libflue.util.dateUtils.toString
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.SIMPLE_DATE_INTERNATIONAL
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.commons.utils.toCalendar
import br.com.mobicare.cielo.commons.utils.toLocalDate
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferToBankAccountUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferWithKeyUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.models.PixBankAccountStore
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums.PixPeriodRecurrence
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixBankAccountData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixRecurrenceData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixValidateKeyData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferReceiptUiState
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferUiState
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixKeysFactory
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class PixTransferViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val requestPixTransferWithKeyUseCase = mockk<RequestPixTransferWithKeyUseCase>()
    private val requestPixTransferToBankAccountUseCase = mockk<RequestPixTransferToBankAccountUseCase>()
    private val getPixTransferDetailsUseCase = mockk<GetPixTransferDetailsUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    private val transferResultEntity = PixTransactionsFactory.TransferResult.entity
    private val transferDetailEntity = PixTransactionsFactory.TransferDetail.entity
    private val apiException = CieloAPIException.networkError(EMPTY)
    private val errorResult = CieloDataResult.APIError(apiException)
    private val emptyResult = CieloDataResult.Empty()
    private val token = EMPTY
    private val validateKeyData = PixValidateKeyData(PixKeysFactory.pixValidateKey)
    private val bankAccountData = PixBankAccountData(PixBankAccountStore())

    private lateinit var viewModel: PixTransferViewModel
    private lateinit var transferStates: List<PixTransferUiState?>
    private lateinit var receiptStates: List<PixTransferReceiptUiState?>

    @Before
    fun setUp() {
        viewModel =
            PixTransferViewModel(
                getUserObjUseCase,
                requestPixTransferWithKeyUseCase,
                requestPixTransferToBankAccountUseCase,
                getPixTransferDetailsUseCase,
                getFeatureTogglePreferenceUseCase,
            )
        transferStates = viewModel.uiState.captureValues()
        receiptStates = viewModel.receiptState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    private fun mockAndRunTransferWithKeyTest(
        result: CieloDataResult<PixTransferResult>,
        validateKeyData: PixValidateKeyData,
        schedulingDate: Calendar? = null,
        recurrenceData: PixRecurrenceData? = null,
    ) {
        // given
        coEvery { requestPixTransferWithKeyUseCase(any()) } returns result

        // when
        viewModel.run {
            keyData = validateKeyData
            if (recurrenceData == null) {
                setSchedulingDate(schedulingDate)
            } else {
                saveRecurrenceData(recurrenceData)
                selectPixRecurrence(true)
            }
            requestTransfer(token)
        }

        dispatcherRule.advanceUntilIdle()
    }

    private fun mockAndRunTransferToBankAccountTest(
        result: CieloDataResult<PixTransferResult>,
        bankAccountData: PixBankAccountData,
        schedulingDate: Calendar? = null,
        recurrenceData: PixRecurrenceData? = null,
    ) {
        // given
        coEvery { requestPixTransferToBankAccountUseCase(any()) } returns result

        // when
        viewModel.run {
            keyData = bankAccountData
            if (recurrenceData == null) {
                setSchedulingDate(schedulingDate)
            } else {
                saveRecurrenceData(recurrenceData)
                selectPixRecurrence(true)
            }
            requestTransfer(token)
        }

        dispatcherRule.advanceUntilIdle()
    }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TransferSent`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.Success(transferResultEntity),
                validateKeyData = validateKeyData,
                schedulingDate = null,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferSent::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TransferSent with recurrence enabled`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.Success(transferResultEntity),
                validateKeyData = validateKeyData,
                schedulingDate = null,
                recurrenceData =
                    PixRecurrenceData(
                        startDate = Calendar.getInstance(),
                        period = PixPeriodRecurrence.WEEKLY,
                    ),
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferSent::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TransferScheduled`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.Success(transferResultEntity),
                validateKeyData = validateKeyData,
                schedulingDate = Calendar.getInstance().plusDays (1),
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferScheduled::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TransferScheduled with recurrence enabled`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.Success(transferResultEntity),
                validateKeyData = validateKeyData,
                schedulingDate = Calendar.getInstance().plusDays (1),
                recurrenceData =
                    PixRecurrenceData(
                        startDate = Calendar.getInstance().plusDays(1),
                        period = PixPeriodRecurrence.WEEKLY,
                    ),
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferScheduled::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_TransferSent`() =
        runTest {
            mockAndRunTransferToBankAccountTest(
                result = CieloDataResult.Success(transferResultEntity),
                bankAccountData = bankAccountData,
                schedulingDate = null,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferSent::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_TransferScheduled`() =
        runTest {
            mockAndRunTransferToBankAccountTest(
                result = CieloDataResult.Success(transferResultEntity),
                bankAccountData = bankAccountData,
                schedulingDate =Calendar.getInstance().plusDays (1),
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TransferScheduled::class.java)
        }

    @Test
    fun `it should set PixTransferUiState_GenericError if keyData is null`() =
        runTest {
            // when
            viewModel.run {
                keyData = null
                requestTransfer(token)
            }

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.GenericError::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_GenericError on empty result`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = emptyResult,
                validateKeyData = validateKeyData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.GenericError::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_GenericError on empty result`() =
        runTest {
            mockAndRunTransferToBankAccountTest(
                result = emptyResult,
                bankAccountData = bankAccountData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.GenericError::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TokenError`() =
        runTest {
            val tokenApiException =
                apiException.also {
                    it.newErrorMessage.flagErrorCode = Text.OTP
                }

            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.APIError(tokenApiException),
                validateKeyData = validateKeyData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TokenError::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_TokenError`() =
        runTest {
            val tokenApiException =
                apiException.also {
                    it.newErrorMessage.flagErrorCode = Text.OTP
                }

            mockAndRunTransferToBankAccountTest(
                result = CieloDataResult.APIError(tokenApiException),
                bankAccountData = bankAccountData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TokenError::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_TooManyRequestsError`() =
        runTest {
            val tooManyRequestsApiException =
                apiException.also {
                    it.newErrorMessage.flagErrorCode = ERROR_CODE_TOO_MANY_REQUESTS
                }

            mockAndRunTransferWithKeyTest(
                result = CieloDataResult.APIError(tooManyRequestsApiException),
                validateKeyData = validateKeyData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TooManyRequestsError::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_TooManyRequestsError`() =
        runTest {
            // given
            val tooManyRequestsApiException =
                apiException.also {
                    it.newErrorMessage.flagErrorCode = ERROR_CODE_TOO_MANY_REQUESTS
                }

            mockAndRunTransferToBankAccountTest(
                result = CieloDataResult.APIError(tooManyRequestsApiException),
                bankAccountData = bankAccountData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.TooManyRequestsError::class.java)
        }

    @Test
    fun `it should call requestTransferWithKey and set PixTransferUiState_GenericError`() =
        runTest {
            mockAndRunTransferWithKeyTest(
                result = errorResult,
                validateKeyData = validateKeyData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.GenericError::class.java)
        }

    @Test
    fun `it should call requestTransferToBankAccount and set PixTransferUiState_GenericError`() =
        runTest {
            mockAndRunTransferToBankAccountTest(
                result = errorResult,
                bankAccountData = bankAccountData,
            )

            // then
            assertThat(transferStates[0]).isInstanceOf(PixTransferUiState.GenericError::class.java)
        }

    // =======================
    // getTransferDetails
    // =======================

    private fun assertReceiptLoadingState(state: PixTransferReceiptUiState?) {
        assertThat(state).isInstanceOf(PixTransferReceiptUiState.Loading::class.java)
    }

    @Test
    fun `it should set PixTransferReceiptUiState_Success on getTransferDetails call`() =
        runTest {
            // given
            coEvery { getPixTransferDetailsUseCase(any()) } returns CieloDataResult.Success(transferDetailEntity)

            // when
            viewModel.getTransferDetails()

            // then
            dispatcherRule.advanceUntilIdle()

            assertReceiptLoadingState(receiptStates[0])
            assertThat(receiptStates[1]).isInstanceOf(PixTransferReceiptUiState.Success::class.java)
            assertThat(
                (receiptStates[1] as PixTransferReceiptUiState.Success).result,
            ).isEqualTo(transferDetailEntity)
        }

    @Test
    fun `it should set PixTransferReceiptUiState_Error on getTransferDetails call when result is empty`() =
        runTest {
            // given
            coEvery { getPixTransferDetailsUseCase(any()) } returns emptyResult

            // when
            viewModel.getTransferDetails()

            // then
            dispatcherRule.advanceUntilIdle()

            assertReceiptLoadingState(receiptStates[0])
            assertThat(receiptStates[1]).isInstanceOf(PixTransferReceiptUiState.Error::class.java)
        }

    @Test
    fun `it should set PixTransferReceiptUiState_Error on getTransferDetails call`() =
        runTest {
            // given
            coEvery { getPixTransferDetailsUseCase(any()) } returns errorResult

            // when
            viewModel.getTransferDetails()

            // then
            dispatcherRule.advanceUntilIdle()

            assertReceiptLoadingState(receiptStates[0])
            assertThat(receiptStates[1]).isInstanceOf(PixTransferReceiptUiState.Error::class.java)
        }

    // =======================
    // getFeatureToggleRecurrence
    // =======================
    @Test
    fun `it should set true in ftRecurrenceEnabled`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PIX_SHOW_BUTTON_TRANSFER_RECURRENCE)
            } returns CieloDataResult.Success(true)

            val states = viewModel.ftRecurrenceEnabled.captureValues()

            viewModel.getFeatureToggleRecurrence()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(true, states[ZERO])
        }

    @Test
    fun `it should set false in ftRecurrenceEnabled`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PIX_SHOW_BUTTON_TRANSFER_RECURRENCE)
            } returns CieloDataResult.Success(false)

            val states = viewModel.ftRecurrenceEnabled.captureValues()

            viewModel.getFeatureToggleRecurrence()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(false, states[ZERO])
        }

    @Test
    fun `setSchedulingDate updates store with given date when date is not today`() =
        runTest {
            val notToday = Calendar.getInstance().plusDays (1)
            viewModel.setSchedulingDate(notToday)
            assertEquals(notToday, viewModel.store.schedulingDate)
        }

    @Test
    fun `setSchedulingDate does not update store when date is today`() =
        runTest {
            val today = Calendar.getInstance()
            viewModel.setSchedulingDate(today)
            assertTrue(viewModel.store.schedulingDate == null)
        }

    @Test
    fun `saveRecurrenceData updates store with given recurrence data`() =
        runTest {
            val recurrenceData = PixRecurrenceData()
            viewModel.saveRecurrenceData(recurrenceData)
            assertEquals(recurrenceData, viewModel.store.recurrenceData)
        }

    @Test
    fun `selectPixRecurrence updates pixRecurrenceIsSelected with true`() =
        runTest {
            viewModel.selectPixRecurrence(true)
            assertTrue(viewModel.pixRecurrenceIsSelected)
        }

    @Test
    fun `selectPixRecurrence updates pixRecurrenceIsSelected with false`() =
        runTest {
            viewModel.selectPixRecurrence(false)
            assertFalse(viewModel.pixRecurrenceIsSelected)
        }

    @Test
    fun `schedulingDateRequest returns startDate when pixRecurrenceIsSelected is true`() {
        val startDate = Calendar.getInstance()
        viewModel.selectPixRecurrence(true)
        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate))

        val result = getSchedulingDateRequest()

        assertEquals(startDate.toString(SIMPLE_DATE_INTERNATIONAL), result)
    }

    @Test
    fun `schedulingDateRequest returns schedulingDate when pixRecurrenceIsSelected is false`() {
        val schedulingDate = Calendar.getInstance().plusDays (1)
        viewModel.selectPixRecurrence(false)
        viewModel.setSchedulingDate(schedulingDate)

        val result = getSchedulingDateRequest()

        assertEquals(schedulingDate.toString(SIMPLE_DATE_INTERNATIONAL), result)
    }

    @Test
    fun `schedulingDateRequest returns null when pixRecurrenceIsSelected is false and schedulingDate is null`() {
        viewModel.selectPixRecurrence(false)
        viewModel.setSchedulingDate(null)

        val result = getSchedulingDateRequest()

        assertNull(result)
    }

    @Test
    fun `frequencyTimeRequest returns period name when pixRecurrenceIsSelected is true`() {
        val periodName = "WEEKLY"
        viewModel.selectPixRecurrence(true)
        viewModel.saveRecurrenceData(PixRecurrenceData(period = PixPeriodRecurrence.WEEKLY))

        val result = getFrequencyTimeRequest()

        assertEquals(periodName, result)
    }

    @Test
    fun `frequencyTimeRequest returns FREQUENCY_TIME_ONE when pixRecurrenceIsSelected is false and isScheduledTransfer is true`() {
        val schedulingDate = Calendar.getInstance().plusDays(1)
        viewModel.selectPixRecurrence(false)
        viewModel.setSchedulingDate(schedulingDate)

        val result = getFrequencyTimeRequest()

        assertEquals("ONE", result)
    }

    @Test
    fun `frequencyTimeRequest returns null when pixRecurrenceIsSelected is false and isScheduledTransfer is false`() {
        viewModel.selectPixRecurrence(false)
        viewModel.setSchedulingDate(null)

        val result = getFrequencyTimeRequest()

        assertNull(result)
    }

    @Test
    fun `schedulingFinalDateRequest returns endDate when pixRecurrenceIsSelected is true and numberOfRepetitions is not null`() {
        val startDate = LocalDate.of(2024, 1, 1)
        viewModel.selectPixRecurrence(true)
        viewModel.saveRecurrenceData(
            PixRecurrenceData(
                startDate = startDate.toCalendar(),
                period = PixPeriodRecurrence.MONTHLY,
                endDate = startDate.toCalendar().plusMonths(2),
            ),
        )

        val result = getSchedulingFinalDateRequest()

        assertEquals(LocalDate.of(2024, 3, 1).toString(), result)
    }

    @Test
    fun `schedulingFinalDateRequest returns endDate when pixRecurrenceIsSelected is true and numberOfRepetitions is null`() {
        val startDate = LocalDate.of(2024, 1, 1)
        viewModel.selectPixRecurrence(true)
        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate.toCalendar(), period = PixPeriodRecurrence.MONTHLY))

        val result = getSchedulingFinalDateRequest()

        assertEquals(startDate.plusYears(2L).toString(), result)
    }

    @Test
    fun `schedulingFinalDateRequest returns null when pixRecurrenceIsSelected is false`() {
        viewModel.selectPixRecurrence(false)

        val result = getSchedulingFinalDateRequest()

        assertNull(result)
    }

    @Test
    fun `getEndDateFromRecurrence returns correct endDate when recurrenceData endDate is null and period is WEEKLY`() {
        val startDate = LocalDate.of(2022, 1, 1)
        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate.toCalendar(), period = PixPeriodRecurrence.WEEKLY))

        val result = getEndDateFromRecurrence()

        assertEquals(startDate.plusWeeks(2 * 52).toString(), result?.toLocalDate().toString())
    }

    @Test
    fun `getEndDateFromRecurrence returns correct endDate when recurrenceData endDate is null and period is not WEEKLY`() {
        val startDate = LocalDate.of(2022, 1, 1)

        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate.toCalendar(), period = PixPeriodRecurrence.MONTHLY))

        val result = getEndDateFromRecurrence()

        assertEquals(startDate.plusYears(2).toString(), result?.toLocalDate().toString())
    }

    @Test
    fun `getEndDateFromRecurrence returns recurrenceData endDate when it is not null`() {
        val startDate = LocalDate.of(2022, 1, 1)
        val endDate = LocalDate.of(2023, 1, 1)

        viewModel.saveRecurrenceData(
            PixRecurrenceData(startDate = startDate.toCalendar(), endDate = endDate.toCalendar(), period = PixPeriodRecurrence.MONTHLY),
        )

        val result = getEndDateFromRecurrence()

        assertEquals(endDate.toString(), result?.toLocalDate().toString())
    }

    @Test
    fun `getMultiplier returns correct multiplier when period is WEEKLY`() {
        val startDate = LocalDate.of(2022, 1, 1)

        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate.toCalendar(), period = PixPeriodRecurrence.WEEKLY))

        val result = getMultiplier()

        assertEquals(2 * 52, result)
    }

    @Test
    fun `getMultiplier returns correct multiplier when period is not WEEKLY`() {
        val startDate = LocalDate.of(2022, 1, 1)

        viewModel.saveRecurrenceData(PixRecurrenceData(startDate = startDate.toCalendar(), period = PixPeriodRecurrence.MONTHLY))

        val result = getMultiplier()

        assertEquals(2 * 12, result)
    }

    private fun getEndDateFromRecurrence(): Calendar? {
        val method = PixTransferViewModel::class.java.getDeclaredMethod("getEndDateFromRecurrence")
        method.isAccessible = true
        return method.invoke(viewModel) as Calendar?
    }

    private fun getMultiplier(): Int? {
        val method = PixTransferViewModel::class.java.getDeclaredMethod("getMultiplier")
        method.isAccessible = true
        return method.invoke(viewModel) as Int?
    }

    private fun getSchedulingDateRequest(): String? {
        val method = PixTransferViewModel::class.java.getDeclaredMethod("schedulingDateRequest")
        method.isAccessible = true
        return method.invoke(viewModel) as String?
    }

    private fun getFrequencyTimeRequest(): String? {
        val method = PixTransferViewModel::class.java.getDeclaredMethod("frequencyTimeRequest")
        method.isAccessible = true
        return method.invoke(viewModel) as String?
    }

    private fun getSchedulingFinalDateRequest(): String? {
        val method = PixTransferViewModel::class.java.getDeclaredMethod("schedulingFinalDateRequest")
        method.isAccessible = true
        return method.invoke(viewModel) as String?
    }
}
