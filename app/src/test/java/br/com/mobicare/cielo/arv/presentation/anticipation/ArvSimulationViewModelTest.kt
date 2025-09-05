package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.domain.model.CardBrand
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.useCase.ConfirmArvAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvBanksUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithFilterUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithValueNewUseCase
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultNotEligibleError
import br.com.mobicare.cielo.arv.utils.UiArvBanksState
import br.com.mobicare.cielo.arv.utils.UiArvConfirmAnticipationState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvSimulationViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val arvBank = ArvFactory.emptyArvBank
    private val arvBankList = ArvFactory.arvBankList
    private val confirmAnticipationResponse = ArvFactory.confirmAnticipationResponse
    private val resultError = ArvFactory.resultError
    private val resultMfaTokenError = ArvFactory.resultMfaTokenError
    private val resultConfirmAnticipationSuccess =
        CieloDataResult.Success(confirmAnticipationResponse)
    private val resultArvBanksSuccess = CieloDataResult.Success(arvBankList)

    private val arvSingleAnticipationWithValueNewUseCase =
        mockk<GetArvSingleAnticipationWithValueNewUseCase>()
    private val arvAnticipationByBrandsUseCase = mockk<GetArvSingleAnticipationWithFilterUseCase>()
    private val getArvBanksUseCase = mockk<GetArvBanksUseCase>()
    private val confirmArvAnticipationUseCase = mockk<ConfirmArvAnticipationUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)

    private lateinit var viewModel: ArvSimulationViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        coEvery { getArvBanksUseCase() } returns resultArvBanksSuccess

        viewModel = ArvSimulationViewModel(
            getArvBanksUseCase,
            confirmArvAnticipationUseCase,
            arvAnticipationByBrandsUseCase,
            arvSingleAnticipationWithValueNewUseCase,
            getUserObjUseCase,
            userPreferences
        )
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set success state on success result of getArvBanks call`() = runTest {
        // given
        coEvery { getArvBanksUseCase() } returns resultArvBanksSuccess

        val uiStateValues = viewModel.banksLiveData.captureValues()

        // when
        viewModel.updateBanks()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiStateValues[uiStateValues.lastIndex - 1])
            .isInstanceOf(UiArvBanksState.ShowLoadingArvBanks::class.java)
        assertThat(uiStateValues.last())
            .isInstanceOf(UiArvBanksState.SuccessArvBanks::class.java)
    }

    @Test
    fun `it should set success state on success result of confirmAnticipation call`() = runTest {

        // given
        coEvery { confirmArvAnticipationUseCase(any()) } returns resultConfirmAnticipationSuccess

        // when
        viewModel.confirmAnticipation(EMPTY, arvBank)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.arvConfirmAnticipationState.value is UiArvConfirmAnticipationState.Success)
    }

    @Test
    fun `it should set error state on error result and null context of confirmAnticipation call`() =
        runTest {
            // given
            coEvery { confirmArvAnticipationUseCase(any()) } returns resultError
            coEvery { getArvBanksUseCase() } returns resultArvBanksSuccess

            // when
            viewModel.confirmAnticipation(EMPTY, arvBank)

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.arvConfirmAnticipationState.value is UiArvConfirmAnticipationState.Error)
        }

    @Test
    fun `it should set show try again state on network error result of getArvBanks call`() =
        runTest {
            // given
            coEvery { getArvBanksUseCase() } returns resultError

            val uiStateValues = viewModel.banksLiveData.captureValues()

            // when
            viewModel.updateBanks()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiStateValues[uiStateValues.lastIndex - 1])
                .isInstanceOf(UiArvBanksState.ShowLoadingArvBanks::class.java)
            assertThat(uiStateValues.last())
                .isInstanceOf(UiArvBanksState.ShowTryAgain::class.java)
        }

    @Test
    fun `it should set error state on network error result of confirmAnticipation call`() =
        runTest {
            coEvery { getArvBanksUseCase() } returns resultArvBanksSuccess

            // given
            coEvery { confirmArvAnticipationUseCase(any()) } returns resultError

            // when
            viewModel.confirmAnticipation(EMPTY, arvBank)

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.arvConfirmAnticipationState.value is UiArvConfirmAnticipationState.Error)
        }

    @Test
    fun `it should set token error state on MFA token error result of confirmAnticipation call`() =
        runTest {
            // given
            coEvery { confirmArvAnticipationUseCase(any()) } returns resultMfaTokenError

            // when
            viewModel.confirmAnticipation(EMPTY, arvBank)

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.arvConfirmAnticipationState.value is UiArvConfirmAnticipationState.ErrorToken)
        }

    @Test
    fun `it should set not eligible error state on error result of confirmAnticipation call`() =
        runTest {
            // given
            coEvery { confirmArvAnticipationUseCase(any()) } returns resultNotEligibleError

            // when
            viewModel.confirmAnticipation(EMPTY, arvBank)

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.arvConfirmAnticipationState.value is UiArvConfirmAnticipationState.ErrorNotEligible)
        }

    @Test
    fun `should list selected acquirers`() {
        //given
        viewModel.updateSimulationData(
            ArvFactory.arvSingleAnticipation.copy(
                acquirers = listOf(
                    ArvFactory.acquirer.copy(name = "REDE"),
                    ArvFactory.acquirer.copy(name = "PAGSEGURO"),
                )
            )
        )
        //when
        val list = viewModel.selectedAcquirersNameList()

        //then
        assert(listOf("REDE", "PAGSEGURO") == list)
    }

    @Test
    fun `should list selected brands`() {
        //given
        viewModel.updateSimulationData(
            ArvFactory.arvSingleAnticipation.copy(
                acquirers = listOf(
                    ArvFactory.acquirer.copy(
                        cardBrands = listOf(
                            CardBrand(
                                name = "AMEX"
                            ),
                            CardBrand(
                                name = "VISA"
                            ),
                            CardBrand(
                                name = "MASTERCARD",
                                isSelected = false
                            )
                        )
                    ),
                )
            )
        )
        //when
        val list = viewModel.selectedBrandsNameList()

        //then
        assert(listOf("AMEX", "VISA") == list)
    }

    @Test
    fun `test handleBankSelect with different receiveToday status`() {
        val response = mockk<ArvAnticipation>()
        val arvBankWithReceiveToday = ArvBank(
            receiveToday = false
        )
        val arvBankWithoutReceiveToday = ArvBank(
            receiveToday = true
        )

        coEvery {
            arvAnticipationByBrandsUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns CieloDataResult.Success(
            response
        )

        viewModel.handleBankSelect(arvBankWithReceiveToday, arvBankWithoutReceiveToday.receiveToday)

        coVerify {
            arvAnticipationByBrandsUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                any(),
                arvBankWithReceiveToday.receiveToday,
            )
        }
    }

    @Test
    fun `test handleBankSelect with same receiveToday status`() {
        val response = mockk<ArvAnticipation>()
        val arvBankWithReceiveToday = ArvBank(
            receiveToday = true
        )
        val arvBankWithoutReceiveToday = ArvBank(
            receiveToday = true
        )

        coEvery {
            arvAnticipationByBrandsUseCase.invoke(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns CieloDataResult.Success(
            response
        )

        viewModel.handleBankSelect(arvBankWithReceiveToday, arvBankWithoutReceiveToday.receiveToday)

        coVerify(inverse = true) {
            arvAnticipationByBrandsUseCase.invoke(
                any(), any(), any(), any(), any(), any()
            )
        }
    }
}