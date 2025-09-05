package br.com.mobicare.cielo.arv.presentation.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.arv.utils.UiArvOnboardingState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
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
class ArvOnboardingViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val saveUserViewHistoryUseCase = mockk<SaveUserViewHistoryUseCase>()

    private lateinit var viewModel: ArvOnboardingViewModel

    @Before
    fun setUp() {
        viewModel = ArvOnboardingViewModel(saveUserViewHistoryUseCase)
    }

    @Test
    fun `it should set UiArvOnboardingState as ShowHome when the CieloDataResult is true`() = runTest {
        // given
        coEvery { saveUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(true)

        // when
        viewModel.userViewArvOnboarding()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.arvOnboardingLiveData.value).isEqualTo(UiArvOnboardingState.ShowHome)
    }

    @Test
    fun `it should set UiArvOnboardingState as ShowHome when the CieloDataResult is false`() = runTest {
        // given
        coEvery { saveUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(false)

        // when
        viewModel.userViewArvOnboarding()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.arvOnboardingLiveData.value).isEqualTo(UiArvOnboardingState.ShowHome)
    }

    @Test
    fun `it should set UiArvOnboardingState as ShowHome when the CieloDataResult is empty`() = runTest {
        // given
        coEvery { saveUserViewHistoryUseCase(any()) } returns CieloDataResult.Empty()

        // when
        viewModel.userViewArvOnboarding()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.arvOnboardingLiveData.value).isEqualTo(UiArvOnboardingState.ShowHome)
    }
}