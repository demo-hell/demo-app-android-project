package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackFiltersUseCase
import br.com.mobicare.cielo.chargeback.presentation.filters.ChargebackFiltersViewModel
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class ChargebackFiltersViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCase = mockk<GetChargebackFiltersUseCase>()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = ChargebackFactory.filterSuccessResponse
    private lateinit var viewModel: ChargebackFiltersViewModel


    @Before
    fun setup() {
        viewModel = ChargebackFiltersViewModel(useCase)
    }


    @Test
    fun `it should return success when load chargeback available filters`() = runBlocking {

        coEvery { useCase.invoke() } returns resultSuccess

        viewModel.getChargebackFilters()

        assert(viewModel.uiState.value is UiState.Success)
    }


    @Test
    fun `it should return error when load chargeback available filters`() = runBlocking {
         
        coEvery { useCase.invoke() } returns resultError

        viewModel.getChargebackFilters()
           
        assert(viewModel.uiState.value is UiState.Error)
    }
}