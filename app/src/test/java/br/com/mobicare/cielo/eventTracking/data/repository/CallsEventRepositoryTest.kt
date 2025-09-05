package br.com.mobicare.cielo.eventTracking.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.eventTracking.CallsMockedData
import br.com.mobicare.cielo.eventTracking.data.datasource.CallsEventRemoteDataSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CallsEventRepositoryTest {

    private val remoteDatasource = mockk<CallsEventRemoteDataSource>()

    private val resultSuccess = CallsMockedData.callsMockedList

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val repository = CallsEventRepositoryImpl(remoteDatasource)

    @Test
    fun `it should return error when getCallsEventsList`() = runTest {
        coEvery { repository.getCallsEventsList(null, null, null, null) } returns resultError

        val result = repository.getCallsEventsList(null, null, null, null)

        assert(result == resultError)
    }

    @Test
    fun `it should return a callsRequestList when getCallsEventsList`() = runTest {
        coEvery { repository.getCallsEventsList(null, null, null, null) } returns CieloDataResult.Success(resultSuccess)

        val result = repository.getCallsEventsList(null, null, null, null)

        assert(result == CieloDataResult.Success(resultSuccess))
    }
}