package br.com.mobicare.cielo.pixMVVM.presentation.infringement

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixEligibilityInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.PixInfringementSelectReasonViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.utils.UIPixInfringementSelectReasonState
import br.com.mobicare.cielo.pixMVVM.utils.PixInfringementFactory
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixInfringementSelectReasonViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixEligibilityInfringementUseCase = mockk<GetPixEligibilityInfringementUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PixInfringementSelectReasonViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultSuccessGetInfringement =
        CieloDataResult.Success(PixInfringementFactory.pixGetInfringementResponse)
    private val resultSuccessGetInfringementWithIneligible =
        CieloDataResult.Success(PixInfringementFactory.pixGetInfringementResponseWithIneligible)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup() {
        viewModel = PixInfringementSelectReasonViewModel(
            getUserObjUseCase,
            getPixEligibilityInfringementUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set uiState with UIPixInfringementSelectReasonStateSuccess when start method is called with is isEligible true`() =
        runTest {
            coEvery {
                getPixEligibilityInfringementUseCase(any())
            } returns resultSuccessGetInfringement

            val states = viewModel.uiState.captureValues()

            viewModel.start(PixInfringementFactory.idEndToEnd)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSelectReasonState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSelectReasonState.HideLoading)
            assert(states[TWO] is UIPixInfringementSelectReasonState.Success)
        }

    @Test
    fun `it should set uiState with UIPixInfringementSelectReasonStateSuccess when start method is called with is isEligible false`() =
        runTest {
            coEvery {
                getPixEligibilityInfringementUseCase(any())
            } returns resultSuccessGetInfringementWithIneligible

            val states = viewModel.uiState.captureValues()

            viewModel.start(PixInfringementFactory.idEndToEnd)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSelectReasonState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSelectReasonState.HideLoading)
            assert(states[TWO] is UIPixInfringementSelectReasonState.Ineligible)
        }

    @Test
    fun `it should set uiState with UIPixInfringementSelectReasonStateError when start method is called with error`() =
        runTest {
            coEvery {
                getPixEligibilityInfringementUseCase(any())
            } returns resultError

            val states = viewModel.uiState.captureValues()

            viewModel.start(PixInfringementFactory.idEndToEnd)

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSelectReasonState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSelectReasonState.HideLoading)
            assert(states[TWO] is UIPixInfringementSelectReasonState.Error)
        }

    @Test
    fun `it should set uiState with UIPixInfringementSelectReasonStateError when start method is called with empty error`() =
        runTest {
            coEvery {
                getPixEligibilityInfringementUseCase(any())
            } returns resultEmpty

            val states = viewModel.uiState.captureValues()

            viewModel.start(PixInfringementFactory.idEndToEnd)

            dispatcherRule.advanceUntilIdle()

            assertEquals(THREE, states.size)

            assert(states[ZERO] is UIPixInfringementSelectReasonState.ShowLoading)
            assert(states[ONE] is UIPixInfringementSelectReasonState.HideLoading)
            assert(states[TWO] is UIPixInfringementSelectReasonState.Error)
        }

}