package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithFilterUseCase
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.UiArvBrandsSelectionState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
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
class ArvFilterSelectionViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val arvAnticipation = ArvFactory.arvSingleAnticipation
    private val selectedBrands = ArvFactory.arvOnlySelectedBrandList
    private val resultError = ArvFactory.resultError
    private val resultArvAnticipationByBrandsSuccess = CieloDataResult.Success(arvAnticipation)

    private val getArvAnticipationByBrandsUseCase = mockk<GetArvSingleAnticipationWithFilterUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private lateinit var viewModel: ArvFilterSelectionViewModel

    private val context = mockk<Context>()

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context

        viewModel = ArvFilterSelectionViewModel(
            getArvAnticipationByBrandsUseCase,
            getUserObjUseCase
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set success state on success result of update anticipation call`() = runTest {
        // given
        coEvery {
            getArvAnticipationByBrandsUseCase(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns resultArvAnticipationByBrandsSuccess

        val uiStateValues = viewModel.arvBrandsSelectionLiveData.captureValues()

        // when
        viewModel.updateAnticipation(arvAnticipation, selectedBrands, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiStateValues[uiStateValues.lastIndex - 2])
            .isInstanceOf(UiArvBrandsSelectionState.ShowLoadingAnticipation::class.java)
        assertThat(uiStateValues[uiStateValues.lastIndex - 1])
            .isInstanceOf(UiArvBrandsSelectionState.HideLoadingAnticipation::class.java)
        assertThat(uiStateValues.last())
            .isInstanceOf(UiArvBrandsSelectionState.SuccessLoadArvAnticipation::class.java)
    }

    @Test
    fun `it should update selected brands and keep not selected ones`() = runTest {

        val previousAnticipation = arvAnticipation.copy(
            acquirers = listOf(
                ArvFactory.acquirer.copy(
                    cardBrands = ArvFactory.arvAllSelectedBrandsList
                )
            )
        )

        val result = CieloDataResult.Success(
            arvAnticipation.copy(
                acquirers = listOf(
                    ArvFactory.acquirer.copy(
                        cardBrands = selectedBrands
                    )
                )
            )
        )
        // given
        coEvery {
            getArvAnticipationByBrandsUseCase(
                any(),
                any(),
                any(),
                brandCodes = selectedBrands.mapNotNull { it.code },
                any(),
                true)
        } returns result

        val uiStateValues = viewModel.arvBrandsSelectionLiveData.captureValues()

        // when
        viewModel.updateAnticipation(previousAnticipation, ArvFactory.arvAMEXNotSelectedBrandsList, true)

        // then
        dispatcherRule.advanceUntilIdle()

        val successState =
            uiStateValues.last() as UiArvBrandsSelectionState.SuccessLoadArvAnticipation
        val brands = successState.anticipation.acquirers?.first()?.cardBrands

        assertThat(brands?.size).isEqualTo(3)
        assertThat(brands?.get(0)?.isSelected).isTrue()
        assertThat(brands?.get(1)?.isSelected).isTrue()
        assertThat(brands?.get(2)?.isSelected).isFalse()
    }

    @Test
    fun `it should update selected acquirers and keep not selected ones`() = runTest {

        val previousAnticipation = arvAnticipation.copy(
            negotiationType = ArvConstants.MARKET_NEGOTIATION_TYPE,
            acquirers = listOf(
                ArvFactory.acquirer.copy(
                    code = 1,
                    isSelected = false
                ),
                ArvFactory.acquirer.copy(
                    code = 2,
                    isSelected = false
                ),
                ArvFactory.acquirer.copy(
                    code = 3
                )
            )
        )

        val result = CieloDataResult.Success(
            arvAnticipation.copy(
                negotiationType = ArvConstants.MARKET_NEGOTIATION_TYPE,
                acquirers = listOf(
                    ArvFactory.acquirer.copy(
                        code = 3
                    )
                )
            )
        )
        // given
        coEvery {
            getArvAnticipationByBrandsUseCase(
                any(),
                any(),
                any(),
                any(),
                acquirerCode = listOf(3),
                true)
        } returns result

        val uiStateValues = viewModel.arvBrandsSelectionLiveData.captureValues()

        // when
        viewModel.updateAnticipation(previousAnticipation, listOf(
            ArvFactory.acquirer.copy(
                code = 1,
                isSelected = false
            ),
            ArvFactory.acquirer.copy(
                code = 2,
                isSelected = false
            ),
            ArvFactory.acquirer.copy(
                code = 3
            )
        ), true)

        // then
        dispatcherRule.advanceUntilIdle()

        val successState =
            uiStateValues.last() as UiArvBrandsSelectionState.SuccessLoadArvAnticipation
        val acquirers = successState.anticipation.acquirers

        assertThat(acquirers?.size).isEqualTo(3)
        assertThat(acquirers?.get(0)?.isSelected).isTrue()
        assertThat(acquirers?.get(1)?.isSelected).isFalse()
        assertThat(acquirers?.get(2)?.isSelected).isFalse()
    }


    @Test
    fun `it should set show error state on network error result of update anticipation call`() =
        runTest {
            // given
            coEvery {
                getArvAnticipationByBrandsUseCase(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns resultError

            val uiStateValues = viewModel.arvBrandsSelectionLiveData.captureValues()

            // when
            viewModel.updateAnticipation(arvAnticipation, selectedBrands, true)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiStateValues[uiStateValues.lastIndex - 2])
                .isInstanceOf(UiArvBrandsSelectionState.ShowLoadingAnticipation::class.java)

            assertThat(uiStateValues[uiStateValues.lastIndex - 1])
                .isInstanceOf(UiArvBrandsSelectionState.HideLoadingAnticipation::class.java)
            assertThat(uiStateValues.last())
                .isInstanceOf(UiArvBrandsSelectionState.ShowError::class.java)
        }
}