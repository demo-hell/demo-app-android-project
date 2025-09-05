package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.utils.*
import br.com.mobicare.cielo.arv.utils.ArvFactory.anticipation
import br.com.mobicare.cielo.arv.utils.ArvFactory.arvSingleAnticipation
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

@OptIn(ExperimentalCoroutinesApi::class)
class ArvSingleAnticipationViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val arvSingleAnticipationWithDateNewUseCase =
        mockk<GetArvSingleAnticipationWithDateNewUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()

    private val context = mockk<Context>()
    private var calendar = mockk<Calendar>(relaxed = true)

    private lateinit var viewModel: ArvSingleAnticipationViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context


        mockkStatic(Calendar::class)
        calendar = GregorianCalendar(2024, 1, 1)
        every { Calendar.getInstance() } returns calendar

        mockkObject(ARVUtils)
        every { ARVUtils.minAnticipationRangeDate } returns DataCustomNew().apply {
            setDate(2024, 1, 1)
        }

        every { ARVUtils.maxAnticipationRangeDate } returns DataCustomNew().apply {
            setDate(2026, 1, 1)
        }

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

        viewModel = ArvSingleAnticipationViewModel(
            arvSingleAnticipationWithDateNewUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `it should set UiArvSingleWithDateState as ErrorArvSingleWithDate when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery {
                arvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.Empty()

            val uiState = viewModel.arvSingleAnticipationWithDataLiveData.captureValues()

            // when
            viewModel.getArvSingleAnticipationWithDate()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvSingleWithDateState.ShowLoadingArvSingleWithDate)
            assertThat(uiState[1]).isEqualTo(UiArvSingleWithDateState.HideLoadingArvSingleWithDate)

            assertThat(uiState[2]).isEqualTo(
                UiArvSingleWithDateState.ErrorArvSingleWithDateMessage(R.string.anticipation_error)
            )
        }

    @Test
    fun `it should set UiArvSingleWithDateState as SuccessArvSingleWithDate`() = runTest {
        // given
        coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                CieloDataResult.Success(anticipation)

        val uiState = viewModel.arvSingleAnticipationWithDataLiveData.captureValues()

        // when
        viewModel.receivableType = "CIELO"
        viewModel.getArvSingleAnticipationWithDate()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvSingleWithDateState.ShowLoadingArvSingleWithDate)
        assertThat(uiState[1]).isEqualTo(UiArvSingleWithDateState.HideLoadingArvSingleWithDate)

        assertThat(uiState[2]).isEqualTo(
            UiArvSingleWithDateState.SuccessArvSingleWithDate(anticipation)
        )
    }

    @Test
    fun `it should set UiArvSingleWithDateState as ErrorArvSingleWithDate`() = runTest {
        // given
        coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns resultError

        val uiState = viewModel.arvSingleAnticipationWithDataLiveData.captureValues()

        // when
        viewModel.getArvSingleAnticipationWithDate()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvSingleWithDateState.ShowLoadingArvSingleWithDate)
        assertThat(uiState[1]).isEqualTo(UiArvSingleWithDateState.HideLoadingArvSingleWithDate)

        assertThat(uiState[2]).isEqualTo(
            UiArvSingleWithDateState.ErrorArvSingleWithDate(
                resultError.apiException.newErrorMessage,
                R.string.anticipation_error
            )
        )
    }


    @Test
    fun `it should set UiArvSingleWithDateState as SuccessArvSingleWithDate when the CieloDataResult is Success and the response is not null`() =
        runTest {
            // given
            coEvery {
                arvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.Success(arvSingleAnticipation)

            val uiState = viewModel.arvSingleAnticipationWithDataLiveData.captureValues()

            // when
            viewModel.receivableType = "CIELO"
            viewModel.getArvSingleAnticipationWithDate()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvSingleWithDateState.ShowLoadingArvSingleWithDate)
            assertThat(uiState[1]).isEqualTo(UiArvSingleWithDateState.HideLoadingArvSingleWithDate)
            assertThat(viewModel.arvSingleAnticipationWithDataLiveData.value).isEqualTo(
                UiArvSingleWithDateState.SuccessArvSingleWithDate(
                    ArvAnticipation(
                        acquirers = listOf(ArvFactory.acquirer),
                        discountAmount = 1.00,
                        finalDate = "2024-03-28",
                        grossAmount = 65.00,
                        id = "ID",
                        initialDate = "2024-03-28",
                        negotiationType = "CIELO",
                        netAmount = 0.13,
                        nominalFee = 0.00,
                        standardFee = 1.40,
                        token = "abc",
                        effectiveFee = 0.10
                    )
                )
            )
        }

    @Test
    fun `it should set UiArvSingleWithDateState as SuccessArvSingleWithDate when the CieloDataResult is Success and the response is not equal`() =
        runTest {
            // given
            coEvery {
                arvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.Success(
                arvSingleAnticipation
            )
            val uiState = viewModel.arvSingleAnticipationWithDataLiveData.captureValues()

            // when
            viewModel.getArvSingleAnticipationWithDate()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvSingleWithDateState.ShowLoadingArvSingleWithDate)
            assertThat(uiState[1]).isEqualTo(UiArvSingleWithDateState.HideLoadingArvSingleWithDate)
            assertThat(viewModel.arvSingleAnticipationWithDataLiveData.value).isNotEqualTo(
                UiArvSingleWithDateState.SuccessArvSingleWithDate(
                    ArvAnticipation(
                        acquirers = listOf(ArvFactory.acquirer),
                        discountAmount = 1.00,
                        finalDate = "2024-03-30",
                        grossAmount = 65.00,
                        id = "ID",
                        initialDate = "2024-03-28",
                        negotiationType = "CIELO",
                        netAmount = 0.13,
                        nominalFee = 0.00,
                        standardFee = 1.40,
                        token = "abc",
                        effectiveFee = 0.10
                    )
                )
            )
        }

    @Test
    fun `it should set correct init date and end date when 24 month range is selected`() =
        runTest {
            coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                    CieloDataResult.Success(anticipation)

            // when
            viewModel.fetchAnticipationFixedPeriod(24)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo("01/01/2024")
            assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo("01/01/2026")
        }

    @Test
    fun `it should set correct init date and end date when 12 month range is selected`() =
        runTest {
            coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                    CieloDataResult.Success(anticipation)

            // when
            viewModel.fetchAnticipationFixedPeriod(12)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo("01/01/2024")
            assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo("01/01/2025")
        }

    @Test
    fun `it should set correct init date and end date when 6 month range is selected`() =
        runTest {
            coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                    CieloDataResult.Success(anticipation)

            // when
            viewModel.fetchAnticipationFixedPeriod(6)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo("01/01/2024")
            assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo("01/07/2024")
        }

    @Test
    fun `it should set correct init date and end date when 3 month range is selected`() =
        runTest {
            coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                    CieloDataResult.Success(anticipation)

            // when
            viewModel.fetchAnticipationFixedPeriod(3)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo("01/01/2024")
            assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo("01/04/2024")
        }

    @Test
    fun `it should set correct init date and end date when 1 month range is selected`() =
        runTest {
            coEvery { arvSingleAnticipationWithDateNewUseCase(any(), any(), any()) } returns
                    CieloDataResult.Success(anticipation)

            // when
            viewModel.fetchAnticipationFixedPeriod(1)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo("01/01/2024")
            assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo("01/02/2024")
        }

    @Test
    fun `it should update date range with new init and end dates`() = runTest {
        val newInitDate = "01/02/2024"
        val newEndDate = "01/03/2024"

        viewModel.updateDateRange(newInitDate, newEndDate)

        assertThat(viewModel.arvDateRangeLiveData.value?.first).isEqualTo(newInitDate)
        assertThat(viewModel.arvDateRangeLiveData.value?.second).isEqualTo(newEndDate)
    }

    @Test
    fun `it should update months option when start date is not min anticipation range date`() =
        runTest {
            val newInitDate = "02/01/2024"
            val newEndDate = "02/01/2025"

            viewModel.updateDateRange(newInitDate, newEndDate)

            assertThat(viewModel.monthsDifferenceLiveData.value).isEqualTo(-1)
        }

    @Test
    fun `it should update months option when start date day of month is not equal to end date day of month`() =
        runTest {
            val newInitDate = "01/01/2024"
            val newEndDate = "02/02/2025"

            viewModel.updateDateRange(newInitDate, newEndDate)

            assertThat(viewModel.monthsDifferenceLiveData.value).isEqualTo(-1)
        }
}