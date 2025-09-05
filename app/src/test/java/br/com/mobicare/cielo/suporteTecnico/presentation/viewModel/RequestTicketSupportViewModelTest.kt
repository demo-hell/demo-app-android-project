package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetRequestTicketSupportUseCase
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.utils.SuporteTecnicoFactory
import br.com.mobicare.cielo.suporteTecnico.utils.UIStateRequestTicketSupport
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RequestTicketSupportViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcher = TestDispatcherRule()

    private val getRequestTicketSupportUseCase = mockk<GetRequestTicketSupportUseCase>()
    private lateinit var viewModel: RequestTicketSupportViewModel
    private val responseSuccess = SuporteTecnicoFactory.userOwnerDocumentAuthorized
    private val responseNotAuthorized = SuporteTecnicoFactory.userOwnerDocumentNotAuthorized
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        viewModel = RequestTicketSupportViewModel(getRequestTicketSupportUseCase)
    }

    @Test
    fun `it should set the correct value to success state`() = runTest {

        //given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Success(responseSuccess)

        //when
        viewModel.getMerchant()

        //then
        dispatcher.advanceUntilIdle()

        viewModel.merchantLiveData.value?.let { state ->
            assert(state is UIStateRequestTicketSupport.AuthorizationSuccess)
        }
    }

    @Test
    fun `verifyRequestTicketSupport incorrect value to error authorized state`() = runTest {

        //given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Success(responseNotAuthorized)

        //when
        viewModel.verifyRequestTicketSupport(responseNotAuthorized)

        //then
        dispatcher.advanceUntilIdle()

        assert(viewModel.merchantLiveData.value is UIStateRequestTicketSupport.AuthorizationError)
    }

    @Test
    fun `verifyRequestTicketSupport with correct file content should post Success state`() = runTest {
        //given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Success(responseSuccess)

        //when
        viewModel.verifyRequestTicketSupport(responseSuccess)

        //then
        dispatcher.advanceUntilIdle()

        assert(viewModel.merchantLiveData.value is UIStateRequestTicketSupport.AuthorizationSuccess)
    }

    @Test
    fun `it should set success state on GetRequestTicketSupportUseCase call result`() = runTest {
        // given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Success(responseSuccess)

        // when
        viewModel.getMerchant()

        // then
        dispatcher.advanceUntilIdle()

        assert(viewModel.merchantLiveData.value is UIStateRequestTicketSupport.AuthorizationSuccess)
    }

    @Test
    fun `it should set error state on GetRequestTicketSupportUseCase call result`() = runTest {
        // given
        coEvery { getRequestTicketSupportUseCase() } returns resultError

        // when
        viewModel.getMerchant()

        // then
        dispatcher.advanceUntilIdle()

        assert(viewModel.merchantLiveData.value is UIStateRequestTicketSupport.Error)

    }

    @Test
    fun `it should set empty state on GetRequestTicketSupportUseCase call result`() = runTest {
        // given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Empty()

        // when
        viewModel.getMerchant()

        // then
        dispatcher.advanceUntilIdle()

        assert(viewModel.merchantLiveData.value is UIStateRequestTicketSupport.Empty)

    }

    @Test
    fun `it should set loading state on GetRequestTicketSupportUseCase call result`() = runTest {
        // given
        coEvery { getRequestTicketSupportUseCase() } returns CieloDataResult.Success(responseSuccess)

        val states = mutableListOf<UIStateRequestTicketSupport>()

        // when
        viewModel.merchantLiveData.observeForever { states.add(it) }
        viewModel.getMerchant()

        // then
        dispatcher.advanceUntilIdle()

        assert(states[0] is UIStateRequestTicketSupport.Loading)
        assert(states[1] is UIStateRequestTicketSupport.AuthorizationSuccess)

    }

}