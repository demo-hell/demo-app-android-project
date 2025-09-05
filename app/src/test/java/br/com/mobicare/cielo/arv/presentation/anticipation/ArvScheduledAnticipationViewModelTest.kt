package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.useCase.ConfirmArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledContractUseCase
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultMfaTokenError
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultNotEligibleError
import br.com.mobicare.cielo.arv.utils.UiArvConfirmScheduledAnticipationState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvScheduledAnticipationViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val resultError = ArvFactory.resultError
    private val arvBank = ArvFactory.emptyArvBank
    private val arvScheduledAnticipation = ArvFactory.arvScheduledAnticipation
    private val confirmArvScheduledAnticipationUseCase = mockk<ConfirmArvScheduledAnticipationUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getArvScheduledContractUseCase = mockk<GetArvScheduledContractUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val analytics = mockk<ArvAnalyticsGA4>(relaxed = true)

    private lateinit var viewModel: ArvScheduledConfirmationViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        viewModel = ArvScheduledConfirmationViewModel(
            getArvScheduledContractUseCase,
            confirmArvScheduledAnticipationUseCase,
            getUserObjUseCase,
            userPreferences,
            analytics
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }


    @Test
    fun `it should set success state on success result of confirmAnticipation call`() = runTest {
        // given
        coEvery { confirmArvScheduledAnticipationUseCase(any()) } returns CieloDataResult.Empty()
        viewModel.updateAnticipationDataUpdateBankData(arvBank)
        viewModel.updateAnticipationData(arvScheduledAnticipation)

        // when
        viewModel.confirmAnticipation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmScheduledAnticipationState.value is UiArvConfirmScheduledAnticipationState.Success)
    }

    @Test
    fun `it should set error state on error result and null context of confirmAnticipation call`() = runTest {
        // given
        coEvery { confirmArvScheduledAnticipationUseCase(any()) } returns resultError
        viewModel.updateAnticipationData(arvScheduledAnticipation)

        // when
        viewModel.confirmAnticipation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmScheduledAnticipationState.value is UiArvConfirmScheduledAnticipationState.Error)
    }

    @Test
    fun `it should set error state on network error result of confirmAnticipation call`() = runTest {
        // given
        coEvery { confirmArvScheduledAnticipationUseCase(any()) } returns resultError
        viewModel.updateAnticipationData(arvScheduledAnticipation)
        // when
        viewModel.confirmAnticipation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmScheduledAnticipationState.value is UiArvConfirmScheduledAnticipationState.Error)
    }

    @Test
    fun `it should set token error state on MFA token error result of confirmAnticipation call`() = runTest {
        // given
        coEvery { confirmArvScheduledAnticipationUseCase(any()) } returns resultMfaTokenError
        viewModel.updateAnticipationData(arvScheduledAnticipation)
        // when
        viewModel.confirmAnticipation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmScheduledAnticipationState.value is UiArvConfirmScheduledAnticipationState.ErrorToken)
    }

    @Test
    fun `it should return base64 string on success api call to get contract`() = runTest {
        // given
        coEvery { getArvScheduledContractUseCase(any()) } returns CieloDataResult.Success(ArvScheduleContract("base64", "24Kb"))
        viewModel.updateAnticipationData(arvScheduledAnticipation)

        // when
        val result = viewModel.getArvScheduledContract(arvScheduledAnticipation.rateSchedules?.first()?.name!!)

        // then
        dispatcherRule.advanceUntilIdle()

        assertEquals("base64", result)
    }

    @Test
    fun `it should return null on error api call to get contract`() = runTest {
        // given
        coEvery { getArvScheduledContractUseCase(any()) } returns resultError
        viewModel.updateAnticipationData(arvScheduledAnticipation)

        // when
        val result = viewModel.getArvScheduledContract(arvScheduledAnticipation.rateSchedules?.first()?.name!!)

        // then
        dispatcherRule.advanceUntilIdle()

        assertNull(result)
    }

    @Test
    fun `it should return not eligible error state on not eligible error result of confirmAnticipation call`() = runTest {
        // given
        coEvery { confirmArvScheduledAnticipationUseCase(any()) } returns resultNotEligibleError
        viewModel.updateAnticipationData(arvScheduledAnticipation)

        // when
        viewModel.confirmAnticipation()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmScheduledAnticipationState.value is UiArvConfirmScheduledAnticipationState.ErrorNotEligible)
    }

}