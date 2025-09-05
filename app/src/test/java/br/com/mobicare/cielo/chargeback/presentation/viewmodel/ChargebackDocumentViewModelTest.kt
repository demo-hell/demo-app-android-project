package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocument
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentViewModel
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.chargeback.utils.UiState
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
class ChargebackDocumentViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getChargebackDocumentUseCase = mockk<GetChargebackDocumentUseCase>()

    private val document = ChargebackFactory.documentFilePdf
    private val chargeback = ChargebackFactory.chargebackWithIdOnly
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    private lateinit var viewModel: ChargebackDocumentViewModel

    @Before
    fun setUp() {
        viewModel = ChargebackDocumentViewModel(getChargebackDocumentUseCase)
    }

    @Test
    fun `it should set success state on success result`() = runTest {
        // given
        coEvery { getChargebackDocumentUseCase(any()) } returns CieloDataResult.Success(document)

        // when
        viewModel.getChargebackDocument(chargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Success)
    }

    @Test
    fun `it should set the correct value to success state`() = runTest {
        // given
        coEvery { getChargebackDocumentUseCase(any()) } returns CieloDataResult.Success(document)

        // when
        viewModel.getChargebackDocument(chargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        viewModel.uiState.value.let {
            assert(it is UiState.Success && document == it.data)
        }
    }

    @Test
    fun `it should set error state on error result`() = runTest {
        // given
        coEvery { getChargebackDocumentUseCase(any()) } returns resultError

        // when
        viewModel.getChargebackDocument(chargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.uiState.value is UiState.Error)
    }

    @Test
    fun `it should set loading state before success state`() = runTest {
        // given
        coEvery { getChargebackDocumentUseCase(any()) } returns CieloDataResult.Success(document)

        val states = mutableListOf<UiState<ChargebackDocument>>()

        // when
        viewModel.uiState.observeForever { states.add(it) }
        viewModel.getChargebackDocument(chargeback)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(states[0] is UiState.Loading)
        assert(states[1] is UiState.Success)
    }

}

