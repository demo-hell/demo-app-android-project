package br.com.mobicare.cielo.arv.presentation.router

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.utils.UiArvRouterState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvRouterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserViewHistoryUseCase = mockk<GetUserViewHistoryUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()

    private lateinit var viewModel: ArvRouterViewModel

    @Before
    fun setUp() {
        viewModel =
            ArvRouterViewModel(
                getUserViewHistoryUseCase,
                getFeatureTogglePreference,
            )
    }

    @Test
    fun `it should set UiArvRouterState as ShowHome when the FTPreference is true and tUserViewHistory is true and isn't from home`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(true)

            // when
            viewModel.handleInitialFlow(null)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvRouterLiveData.value).isEqualTo(UiArvRouterState.ShowHome)
        }

    @Test
    fun `it should set UiArvRouterState as ShowOnboarding when the FTPreference is true and tUserViewHistory is false and isn't from home`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(false)

            // when
            viewModel.handleInitialFlow(null)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvRouterLiveData.value)
                .isEqualTo(UiArvRouterState.ShowOnboarding)
        }

    @Test
    fun `it should set UiArvRouterState as ShowOnboarding when the FTPreference is true and tUserViewHistory is empty and isn't from home`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.handleInitialFlow(null)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvRouterLiveData.value)
                .isEqualTo(UiArvRouterState.ShowOnboarding)
        }

    @Test
    fun `it should set UiArvRouterState as ShowUnavailableService when the FeatureTogglePreference is false and isn't from home`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(false)

            // when
            viewModel.handleInitialFlow(null)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvRouterLiveData.value)
                .isEqualTo(UiArvRouterState.ShowUnavailableService)
        }

    @Test
    fun `it should set UiArvRouterState as ShowArvSingleAnticipation when the FeatureTogglePreference is true and is from home`() =
        runTest {
            // given
            val arvAnticipation = ArvAnticipation(isFromCardHomeFlow = true)
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)

            // when
            viewModel.handleInitialFlow(arvAnticipation)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvRouterLiveData.value)
                .isEqualTo(UiArvRouterState.ShowArvSingleAnticipation(arvAnticipation))
        }
}
