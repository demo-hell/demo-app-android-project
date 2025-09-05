package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureToggleUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.domain.Feature
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RaD1MigrationEffectiveTimeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getFeatureToggleUseCase = mockk<GetFeatureToggleUseCase>()

    private lateinit var viewModel: RaD1MigrationEffectiveTimeViewModel


    @Test
    fun `return correct time when success`() =
        runTest {
            // given
            coEvery { getFeatureToggleUseCase("ra_d1_migration") } returns
                    CieloDataResult.Success(Feature(status = "3 dias úteis"))

            viewModel = RaD1MigrationEffectiveTimeViewModel(
                getFeatureToggleUseCase
            )

            // then
            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.effectiveTimeLiveData.value).isEqualTo(
                "3 dias úteis")
        }

    @Test
    fun `return default time when error`() =
        runTest {
            // given
            coEvery { getFeatureToggleUseCase("ra_d1_migration")  } returns
                    CieloDataResult.APIError(
                        CieloAPIException.networkError(EMPTY)
                    )

            // then
            viewModel = RaD1MigrationEffectiveTimeViewModel(
                getFeatureToggleUseCase
            )
            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.effectiveTimeLiveData.value).isEqualTo(
                "3 dias úteis")
        }

    @Test
    fun `return default time when result is empty`() =
        runTest {
            // given
            coEvery { getFeatureToggleUseCase("ra_d1_migration")  } returns
                    CieloDataResult.Empty()

            // then
            viewModel = RaD1MigrationEffectiveTimeViewModel(
                getFeatureToggleUseCase
            )

            dispatcherRule.advanceUntilIdle()

            Truth.assertThat(viewModel.effectiveTimeLiveData.value).isEqualTo(
                "3 dias úteis")
        }
}