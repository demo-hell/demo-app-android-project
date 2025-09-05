package br.com.mobicare.cielo.pixMVVM.presentation.bottomSheets

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1
import br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout.PixAlertNewLayoutViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixAlertNewLayoutViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private lateinit var viewModel: PixAlertNewLayoutViewModel

    private val getUserViewHistoryUseCase = mockk<GetUserViewHistoryUseCase>()
    private val saveUserViewHistoryUseCase = mockk<SaveUserViewHistoryUseCase>()
    private val featureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    @Before
    fun setup() {
        viewModel =
            PixAlertNewLayoutViewModel(
                getUserViewHistoryUseCase,
                saveUserViewHistoryUseCase,
                featureTogglePreferenceUseCase,
            )
    }

    @Test
    fun `verifyShowBottomSheet should set isShowBottomSheet to false when MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED is false and PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1 is false`() =
        runTest {
            coEvery {
                getUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED)
            } returns CieloDataResult.Success(false)

            coEvery {
                featureTogglePreferenceUseCase(PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1)
            } returns CieloDataResult.Success(false)

            val states = viewModel.isShowBottomSheet.captureValues()

            viewModel.verifyShowBottomSheet()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(false, states[ZERO])
        }

    @Test
    fun `verifyShowBottomSheet should set isShowBottomSheet to true when MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED is false and PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1 is true`() =
        runTest {
            coEvery {
                getUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED)
            } returns CieloDataResult.Success(false)

            coEvery {
                featureTogglePreferenceUseCase(PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1)
            } returns CieloDataResult.Success(true)

            val states = viewModel.isShowBottomSheet.captureValues()

            viewModel.verifyShowBottomSheet()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(true, states[ZERO])
        }

    @Test
    fun `verifyShowBottomSheet should set isShowBottomSheet to false when MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED is true and PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1 is false`() =
        runTest {
            coEvery {
                getUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED)
            } returns CieloDataResult.Success(true)

            coEvery {
                featureTogglePreferenceUseCase(PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1)
            } returns CieloDataResult.Success(false)

            val states = viewModel.isShowBottomSheet.captureValues()

            viewModel.verifyShowBottomSheet()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(false, states[ZERO])
        }

    @Test
    fun `verifyShowBottomSheet should set isShowBottomSheet to false when MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED is true and PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1 is true`() =
        runTest {
            coEvery {
                getUserViewHistoryUseCase(MODAL_ALERT_NEW_LAYOUT_PIX_VIEWED)
            } returns CieloDataResult.Success(true)

            coEvery {
                featureTogglePreferenceUseCase(PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1)
            } returns CieloDataResult.Success(true)

            val states = viewModel.isShowBottomSheet.captureValues()

            viewModel.verifyShowBottomSheet()

            dispatcherRule.advanceUntilIdle()

            assertEquals(ONE, states.size)
            assertEquals(false, states[ZERO])
        }
}
