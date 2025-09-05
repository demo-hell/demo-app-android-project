package br.com.mobicare.cielo.arv.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetConfigurationUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvEffectiveTimeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getConfigurationUseCase = mockk<GetConfigurationUseCase>()

    private lateinit var viewModel: ArvEffectiveTimeViewModel


    @Test
    fun `return correct time when success`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase("ARV_EFFECTIVE_TIME", "15h45") } returns
                    CieloDataResult.Success("00h00")

            viewModel = ArvEffectiveTimeViewModel(
                getConfigurationUseCase
            )

            // then
            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.arvEffectiveTimeLiveData.value).isEqualTo(
                "00h00")
        }

    @Test
    fun `return default time when error`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase("ARV_EFFECTIVE_TIME", any())  } returns
                    ArvFactory.resultError

            // then
            viewModel = ArvEffectiveTimeViewModel(
                getConfigurationUseCase
            )
            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.arvEffectiveTimeLiveData.value).isEqualTo(
                "15h45")
        }

    @Test
    fun `return default time when result is empty`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase("ARV_EFFECTIVE_TIME", any())  } returns
                    CieloDataResult.Empty()

            // then
            viewModel = ArvEffectiveTimeViewModel(
                getConfigurationUseCase
            )

            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.arvEffectiveTimeLiveData.value).isEqualTo(
                "15h45")
        }
}