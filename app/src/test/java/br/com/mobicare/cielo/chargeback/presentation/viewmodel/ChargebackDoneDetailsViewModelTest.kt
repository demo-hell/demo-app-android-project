package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackLifecycleUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDoneDetailsViewModel
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChargebackDoneDetailsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getChargebackLifecycleUseCase = mockk<GetChargebackLifecycleUseCase>()

    private val lifecycleList = ChargebackFactory.lifecycleList
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private lateinit var viewModel: ChargebackDoneDetailsViewModel

    @Before
    fun setUp() {
        viewModel = ChargebackDoneDetailsViewModel(getChargebackLifecycleUseCase)
    }

    @Test
    fun `it should set success state on getChargebackLifecycleUseCase call result`() = runTest {
        // given
        coEvery { getChargebackLifecycleUseCase(any()) } returns CieloDataResult.Success(lifecycleList)

        // when
        viewModel.getChargebackLifecycle(ZERO)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `it should set the correct lifecycle list to success state`() = runTest {
        // given
        coEvery { getChargebackLifecycleUseCase(any()) } returns CieloDataResult.Success(lifecycleList)

        // when
        viewModel.getChargebackLifecycle(ZERO)

        // then
        dispatcherRule.advanceUntilIdle()

        viewModel.uiState.value.let {
            assert(it is UiState.Success && lifecycleList == it.data)
        }
    }

    @Test
    fun `it should set error state on getChargebackLifecycleUseCase call result`() = runTest {
        // given
        coEvery { getChargebackLifecycleUseCase(any()) } returns resultError

        // when
        viewModel.getChargebackLifecycle(ZERO)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `it should set loading state before success state on getChargebackLifecycleUseCase call result`() = runTest {
        // given
        coEvery { getChargebackLifecycleUseCase(any()) } returns CieloDataResult.Success(lifecycleList)

        val states = mutableListOf<UiState<List<Lifecycle>>>()

        // when
        viewModel.uiState.observeForever { states.add(it) }
        viewModel.getChargebackLifecycle(ZERO)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(states[0] is UiState.Loading)
        assert(states[1] is UiState.Success)
    }

}

