package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocumentSender
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDocumentSenderUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackDocumentSenderViewModel
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChargebackDocumentSenderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getChargebackDocumentSenderUseCase = mockk<GetChargebackDocumentSenderUseCase>()

    private val document = ChargebackFactory.documentFileSender
    private val chargeback = ChargebackFactory.chargebackWithIdOnly
    private val refundFileInformation = ChargebackFactory.refundFileInformation
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyFileDocument = ChargebackFactory.documentEmptyFileSender
    private val emptyFileNameDocument = ChargebackFactory.documentEmptyNameFileSender
    private val nullDocument = ChargebackFactory.documentFileSenderNull

    private lateinit var viewModel: ChargebackDocumentSenderViewModel

    @Before
    fun setUp() {
        viewModel = ChargebackDocumentSenderViewModel(getChargebackDocumentSenderUseCase)
    }

    @Test
    fun `it should set success state on success result`() = runTest {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)

        // when
        viewModel.getChargebackDocumentSender(chargeback, refundFileInformation)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Success)
    }

    @Test
    fun `it should set loading state before success state`() = runTest {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)

        val states = mutableListOf<UiState<ChargebackDocumentSender>>()

        // when
        viewModel.documentSenderLiveData.observeForever { states.add(it) }
        viewModel.getChargebackDocumentSender(chargeback, refundFileInformation)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(states[0] is UiState.Loading)
        assert(states[1] is UiState.Success)
    }

    @Test
    fun `it should set error state on error result`() = runTest {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns resultError

        // when
        viewModel.getChargebackDocumentSender(chargeback, refundFileInformation)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Error)
    }

    @Test
    fun `it should set the correct value to success state`() = runTest {

        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)

        //when
        viewModel.getChargebackDocumentSender(chargeback, refundFileInformation)

        //then
        dispatcherRule.advanceUntilIdle()

       viewModel.documentSenderLiveData.value.let { state ->

           assert(state is UiState.Success && document == state.data )
       }
    }

    @Test
    fun `verifyDocument with non-null document should post Success state`() = runTest{
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)

        //when
        viewModel.verifyDocument(document)

        //then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Success)
    }

    @Test
    fun `verifyDocument with null document should post Error state`() = runTest {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)

        // when
        viewModel.verifyDocument(nullDocument)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Error)
    }

    @Test
    fun `verifyDocument with empty file content should post Error state`() = runTest {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)


        // when
        viewModel.verifyDocument(emptyFileDocument)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Error)
    }

    @Test
    fun `verifyDocument with empty file name should post Error state`() {
        // given
        coEvery { getChargebackDocumentSenderUseCase(any()) } returns CieloDataResult.Success(document)


        // when
        viewModel.verifyDocument(emptyFileNameDocument)

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.documentSenderLiveData.value is UiState.Error)
    }

}