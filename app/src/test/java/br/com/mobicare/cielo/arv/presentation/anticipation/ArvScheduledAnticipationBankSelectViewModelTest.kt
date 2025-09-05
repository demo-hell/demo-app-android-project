package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.domain.useCase.GetArvBranchContractsUseCase
import br.com.mobicare.cielo.arv.presentation.model.ScheduleContract
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.UiArvBranchesContractsState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledMarketFeatureToggleState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvScheduledAnticipationBankSelectViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val resultError = ArvFactory.resultError
    private val resultArvBranchesContractsSuccess = CieloDataResult.Success(ArvFactory.arvBranchesContracts)

    private val getArvBranchContractsUseCase = mockk<GetArvBranchContractsUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()

    private lateinit var viewModel: ArvScheduledAnticipationBankSelectViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA) } returns
                CieloDataResult.Success(true)

            viewModel = ArvScheduledAnticipationBankSelectViewModel(
                ArvFactory.scheduledAnticipationBankSelectFragmentArgsNotHired,
                getArvBranchContractsUseCase,
                getUserObjUseCase,
                getFeatureTogglePreference
            )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `at init all schedules should be available when none was hired`() = runTest {
        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationAvailableTypeMutableLiveData.value

        assertThat(negotiationType).isEqualTo("BOTH")
    }

    @Test
    fun `at init only market should be available when cielo was hired`() = runTest {
        // given
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsCieloHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )

        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationAvailableTypeMutableLiveData.value

        assertThat(negotiationType).isEqualTo("MARKET")
    }

    @Test
    fun `at init only cielo should be available when market was hired`() = runTest {
        // given
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsMarketHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )

        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationAvailableTypeMutableLiveData.value

        assertThat(negotiationType).isEqualTo("CIELO")
    }

    @Test
    fun `at init cielo negotiation type should be default`() = runTest {
        // given
        coEvery { getArvBranchContractsUseCase.invoke() } returns resultArvBranchesContractsSuccess

       // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationTypeLiveData.value

        assertThat(negotiationType).isEqualTo("CIELO")
    }

    @Test
    fun `at init market negotiation type should be default when cielo was hired`() = runTest {
        //Given
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsCieloHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )
        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationTypeLiveData.value

        assertThat(negotiationType).isEqualTo("MARKET")
    }

    @Test
    fun `at init cielo negotiation type should be default when market was hired`() = runTest {
        //Given
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsMarketHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )
        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationTypeLiveData.value

        assertThat(negotiationType).isEqualTo("CIELO")
    }

    @Test
    fun `it should set success state on success result of getBranchesContracts call`() = runTest {
        // given
        coEvery { getArvBranchContractsUseCase.invoke() } returns resultArvBranchesContractsSuccess

        val uiStateValues = viewModel.arvBranchesContractsStateLiveData.captureValues()

        // when
        viewModel.getBranchesContracts()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiStateValues.first())
            .isInstanceOf(UiArvBranchesContractsState.ShowLoading::class.java)
        assertThat(uiStateValues[1])
            .isInstanceOf(UiArvBranchesContractsState.HideLoading::class.java)
        assertThat(uiStateValues.last())
            .isInstanceOf(
                UiArvBranchesContractsState.SuccessListContracts::class.java)


        assertThat((uiStateValues.last() as UiArvBranchesContractsState.SuccessListContracts).contracts).containsExactly(
                ScheduleContract(
                    cnpj = "09205562000146",
                    schedule = "CIELO",
                    fee = "3,00%",
                    hireDate = "2023-08-29"
                ),
                ScheduleContract(
                    cnpj = "09205562000146",
                    schedule = "MARKET",
                    fee = "3,00%",
                    hireDate = "2023-08-29"
                )
            )
    }

    @Test
    fun `when it is in alreadyShowedState should not call api`() = runTest {
        // given
        viewModel.setAlreadyShowedContracts(true)

        val uiStateValues = viewModel.arvBranchesContractsStateLiveData.captureValues()

        // when
        viewModel.getBranchesContracts()

        // then
        dispatcherRule.advanceUntilIdle()

        coVerify { getArvBranchContractsUseCase.invoke() wasNot called }

        assertThat(uiStateValues.first())
            .isInstanceOf(UiArvBranchesContractsState.AlreadyShowed::class.java)
        assertThat(uiStateValues.size).isEqualTo(1)
    }

    @Test
    fun `it should set error state on error result of getBranchesContracts call`() = runTest {
        // given
        coEvery { getArvBranchContractsUseCase.invoke() } returns resultError

        val uiStateValues = viewModel.arvBranchesContractsStateLiveData.captureValues()

        // when
        viewModel.getBranchesContracts()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiStateValues.first())
            .isInstanceOf(UiArvBranchesContractsState.ShowLoading::class.java)
        assertThat(uiStateValues[1])
            .isInstanceOf(UiArvBranchesContractsState.HideLoading::class.java)
        assertThat(uiStateValues.last())
            .isInstanceOf(
                UiArvBranchesContractsState.ShowError::class.java
            )
    }

    @Test
    fun `it should set market disabled state on disabled feature toggle`() = runTest {
        // given
        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA) } returns
                CieloDataResult.Success(false)

        // when
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsNotHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )

        val uiStateValues = viewModel.arvMarketToggleLiveData.captureValues()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiStateValues.first())
            .isInstanceOf(UiArvScheduledMarketFeatureToggleState.Disabled::class.java)

        assertThat(uiStateValues.size).isEqualTo(1)
    }
    @Test
    fun `at init cielo only should be available when market FT is false`() = runTest {
        // given
        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA) } returns
                CieloDataResult.Success(false)

        // when
        viewModel = ArvScheduledAnticipationBankSelectViewModel(
            ArvFactory.scheduledAnticipationBankSelectFragmentArgsNotHired,
            getArvBranchContractsUseCase,
            getUserObjUseCase,
            getFeatureTogglePreference
        )

        // then
        dispatcherRule.advanceUntilIdle()

        val negotiationType = viewModel.arvNegotiationAvailableTypeMutableLiveData.value

        assertThat(negotiationType).isEqualTo("CIELO")
    }
}