package br.com.mobicare.cielo.component.onboarding.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.component.onboarding.viewModel.BaseOnboardingViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseOnboardingViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val saveUserViewHistoryUseCase = mockk<SaveUserViewHistoryUseCase>()

    private lateinit var viewModel: BaseOnboardingViewModel

    @Before
    fun setup() {
        viewModel = BaseOnboardingViewModel(
            saveUserViewHistoryUseCase
        )
    }

    @Test
    fun `it must change Onboarding state when successfully saving the onboarding preview flag`() =
        runTest {
            coEvery {
                saveUserViewHistoryUseCase(any())
            } returns CieloDataResult.Success(true)

            val uiOnboardingStates = viewModel.uiOnboardingState.captureValues()

            viewModel.saveViewOnboarding(EMPTY_STRING)

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, uiOnboardingStates.size)
        }

    @Test
    fun `it must change Onboarding state when empty return saving the onboarding preview flag`() =
        runTest {
            coEvery {
                saveUserViewHistoryUseCase(any())
            } returns CieloDataResult.Empty()

            val uiOnboardingStates = viewModel.uiOnboardingState.captureValues()

            viewModel.saveViewOnboarding(EMPTY_STRING)

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, uiOnboardingStates.size)
        }

}