package br.com.mobicare.cielo.eventTracking.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.eventTracking.EventMockedData
import br.com.mobicare.cielo.eventTracking.domain.model.MachineRequest
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetDeliveryEventListUseCase
import br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest.MachineRequestViewModel
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import br.com.mobicare.cielo.eventTracking.utils.MachineRequestItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MachineRequestViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getDeliveryEventListUseCase = mockk<GetDeliveryEventListUseCase>()
    private lateinit var machineRequestViewModel: MachineRequestViewModel

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setup() {
        machineRequestViewModel = MachineRequestViewModel(getDeliveryEventListUseCase)
    }

    @Test
    fun `it should return empty machineRequestList on success`() = runTest {
        coEvery { getDeliveryEventListUseCase(any(), any(), any(), any()) } returns CieloDataResult.Success(listOf())

        machineRequestViewModel.startFilter(EventMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        machineRequestViewModel.deliveryEventList.value?.also {
            assert(it is EventsRequestResource.Success && it.data == listOf<MachineRequest>())
        }
    }

    @Test
    fun `it should return machineRequestList on success`() = runTest {
        coEvery { getDeliveryEventListUseCase(any(), any(), any(), any()) } returns CieloDataResult.Success(EventMockedData.myRequestMockedList)

        machineRequestViewModel.startFilter(EventMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        machineRequestViewModel.deliveryEventList.value?.also {
            assert(it is EventsRequestResource.Success && it.data == EventMockedData.myRequestMockedList)
        }
    }

    @Test
    fun `it should set error on error Result`() = runTest {
        coEvery { getDeliveryEventListUseCase(any(), any(), any(), any()) } returns resultError
        machineRequestViewModel.startFilter(EventMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        assert(machineRequestViewModel.deliveryEventList.value is EventsRequestResource.Error)
    }

    @Test
    fun `it should set loading before success`() = runTest {
        coEvery { getDeliveryEventListUseCase(any(), any(), any(), any()) } returns CieloDataResult.Success(EventMockedData.myRequestMockedList)

        val states = mutableListOf<EventsRequestResource<List<MachineRequestItem>>>()

        machineRequestViewModel.deliveryEventList.observeForever { states.add(it) }
        machineRequestViewModel.startFilter(EventMockedData.firstFilter)

        dispatcherRule.advanceUntilIdle()

        assert(states[0] is EventsRequestResource.Loading)
        assert(states[1] is EventsRequestResource.Success)
    }

    @Test
    fun `it should setup start filter`() = runTest {
        machineRequestViewModel.startFilter(EventMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()

        assert(machineRequestViewModel.cieloFilterMachineList.value?.last() == EventMockedData.firstFilter)
    }

    @Test
    fun `it should update last filter`() = runTest {
        machineRequestViewModel.startFilter(EventMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()
        machineRequestViewModel.updateMachineFilter(EventMockedData.updateFilter)
        dispatcherRule.advanceUntilIdle()

        assert(machineRequestViewModel.cieloFilterMachineList.value?.last() == EventMockedData.updateFilter)
    }

    @Test
    fun `it should update last filter and remove it`() = runTest {
        machineRequestViewModel.startFilter(EventMockedData.firstFilter)
        dispatcherRule.advanceUntilIdle()
        machineRequestViewModel.updateMachineFilter(EventMockedData.updateFilter)
        dispatcherRule.advanceUntilIdle()
        machineRequestViewModel.clearLastSelectedFilter()
        dispatcherRule.advanceUntilIdle()

        assert(machineRequestViewModel.cieloFilterMachineList.value?.last() == EventMockedData.firstFilter)
    }
}