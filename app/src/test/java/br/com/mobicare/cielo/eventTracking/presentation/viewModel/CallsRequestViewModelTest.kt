package br.com.mobicare.cielo.eventTracking.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.eventTracking.CallsMockedData
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetCallsEventListUseCase
import br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest.CallsRequestViewModel
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CallsRequestViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getCallsEventListUseCase = mockk<GetCallsEventListUseCase>()
    private lateinit var callsRequestViewModel: CallsRequestViewModel

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        callsRequestViewModel = CallsRequestViewModel(getCallsEventListUseCase)
    }

    @Test
    fun `it should return empty callsRequestList on success`() = runTest {
        coEvery { getCallsEventListUseCase(any(), any(), any(), any()) } returns
                CieloDataResult.Success(emptyList())

        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        callsRequestViewModel.callsEventList.value?.also { callsList ->
            assert(callsList is EventsRequestResource.Success && callsList.data == emptyList<CallRequest>())
        }
    }

    @Test
    fun `it should return callsRequestList on success`() = runTest {
        coEvery { getCallsEventListUseCase(any(), any(), any(), any()) } returns
                CieloDataResult.Success(CallsMockedData.callsMockedList)

        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        callsRequestViewModel.callsEventList.value?.also {
            assert(it is EventsRequestResource.Success && it.data == CallsMockedData.callsMockedList)
        }
    }

    @Test
    fun `it should set error on error Result`() = runTest {
        coEvery { getCallsEventListUseCase(any(), any(), any(), any()) } returns resultError
        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        assert(callsRequestViewModel.callsEventList.value is EventsRequestResource.Error)
    }

    @Test
    fun `it should set loading before success`() = runTest {
        coEvery { getCallsEventListUseCase(any(), any(), any(), any()) } returns CieloDataResult.Success(CallsMockedData.callsMockedList)

        val states = mutableListOf<EventsRequestResource<List<CallRequest>>>()

        callsRequestViewModel.callsEventList.observeForever { states.add(it) }
        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        assert(states[0] is EventsRequestResource.Loading)
        assert(states[1] is EventsRequestResource.Success)
    }

    @Test
    fun `it should setup start filter`() = runTest {
        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()

        assert(callsRequestViewModel.callsFilterList.value?.last() == CallsMockedData.firstFilter)
    }

    @Test
    fun `it should update last filter`() = runTest {
        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()
        callsRequestViewModel.updateCallsFilter(CallsMockedData.updateFilter)
        dispatcherRule.advanceUntilIdle()

        assert(callsRequestViewModel.callsFilterList.value?.last() == CallsMockedData.updateFilter)
    }

    @Test
    fun `it should update last filter and remove it`() = runTest {
        callsRequestViewModel.startFilter(CallsMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()
        callsRequestViewModel.updateCallsFilter(CallsMockedData.updateFilter)
        dispatcherRule.advanceUntilIdle()
        callsRequestViewModel.clearLastSelectedFilter()
        dispatcherRule.advanceUntilIdle()

        assert(callsRequestViewModel.callsFilterList.value?.last() == CallsMockedData.firstFilter)
    }
}