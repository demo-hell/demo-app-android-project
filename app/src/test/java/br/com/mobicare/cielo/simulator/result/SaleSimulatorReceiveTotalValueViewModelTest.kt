package br.com.mobicare.cielo.simulator.result

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureToggleUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.simulator.simulation.presentation.result.SaleSimulatorReceiveTotalValueViewModel
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaleSimulatorReceiveTotalValueViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getFeatureToggleUseCase = mockk<GetFeatureToggleUseCase>()

    private lateinit var viewModel: SaleSimulatorReceiveTotalValueViewModel


    @Test
    fun `return correct values when success`() =
        runTest {
            // given
            coEvery { getFeatureToggleUseCase("showSaleSimulatorReceiveTotalValue") } returns
                    CieloDataResult.Success(Feature(
                        show = true,
                        statusMessage = "MESSAGEM CORRETA DO JURIDICO")
                    )

            viewModel = SaleSimulatorReceiveTotalValueViewModel(
                getFeatureToggleUseCase
            )

            // then
            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.receiveTotalValueLiveData.value).isEqualTo(
                Pair(true, "MESSAGEM CORRETA DO JURIDICO"))
        }

    @Test
    fun `return default values when result is empty`() =
        runTest {
            // given
            coEvery { getFeatureToggleUseCase("showSaleSimulatorReceiveTotalValue")  } returns
                    CieloDataResult.Empty()

            // then
            viewModel = SaleSimulatorReceiveTotalValueViewModel(
                getFeatureToggleUseCase
            )

            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.receiveTotalValueLiveData.value).isEqualTo(
                Pair(false, "Para receber o valor total que simulou, cobre:"))
        }
}