package br.com.mobicare.cielo.eventTracking.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.eventTracking.EventMockedData
import br.com.mobicare.cielo.eventTracking.data.datasource.RequestDeliveryEventRemoteDatasource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DeliveryEventRepositoryTest {

    private val remoteDatasource = mockk<RequestDeliveryEventRemoteDatasource>()

    private val resultSuccess = EventMockedData.myRequestMockedList

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val repository = DeliveryEventRepositoryImpl(remoteDatasource)

    @Test
    fun `it should return error when requestDeliveryEventList`() = runTest {
        coEvery { repository.requestDeliveryEventList(null, null, null, null) } returns resultError

        val result = repository.requestDeliveryEventList(null, null, null, null)

        assert(result == resultError)
    }

    @Test
    fun `it should return a machineRequestList when requestDeliveryEventList`() = runTest {
        coEvery { repository.requestDeliveryEventList(null, null, null, null) } returns CieloDataResult.Success(resultSuccess)

        val result = repository.requestDeliveryEventList(null, null, null, null)

        assert(result == CieloDataResult.Success(resultSuccess))
    }
}