package br.com.mobicare.cielo.eventTracking.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.eventTracking.EventMockedData
import br.com.mobicare.cielo.eventTracking.domain.repository.DeliveryEventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetDeliveryEventListUseCaseTest {
    private val repository = mockk<DeliveryEventRepository>()

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(EventMockedData.myRequestMockedList)

    private val getDeliveryEventListUseCase = GetDeliveryEventListUseCase(repository)


    @Test
    fun `it should call requestDeliveryEventList only once`() = runTest {
        coEvery { repository.requestDeliveryEventList(null, null, null, null) } returns resultSuccess

        getDeliveryEventListUseCase(null, null, null, null)

        coVerify(exactly = 1) { repository.requestDeliveryEventList(null, null, null, null) }
    }

    @Test
    fun `should return a machineRequestList`() = runTest {
        coEvery { repository.requestDeliveryEventList(null, null, null, null) } returns resultSuccess

        val result = getDeliveryEventListUseCase(null, null, null, null)

        assert(result == resultSuccess)
    }

    @Test
    fun `it should return a network error`() = runTest {
        coEvery { repository.requestDeliveryEventList(null, null, null, null) } returns resultError

        val result = getDeliveryEventListUseCase(null, null, null, null)

        assert(result == resultError)
    }
}