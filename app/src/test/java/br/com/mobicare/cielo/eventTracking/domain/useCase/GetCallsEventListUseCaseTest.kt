package br.com.mobicare.cielo.eventTracking.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.eventTracking.CallsMockedData
import br.com.mobicare.cielo.eventTracking.domain.repository.CallsEventRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetCallsEventListUseCaseTest {
    private val repository = mockk<CallsEventRepository>()

    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(CallsMockedData.callsMockedList)

    private val getCallsEventListUseCase = GetCallsEventListUseCase(repository)


    @Test
    fun `it should call getCallsEventsList only once`() = runTest {
        coEvery { repository.getCallsEventsList(null, null, null, null) } returns resultSuccess

        getCallsEventListUseCase(null, null, null, null)

        coVerify(exactly = 1) { repository.getCallsEventsList(null, null, null, null) }
    }

    @Test
    fun `should return a callsRequestList`() = runTest {
        coEvery { repository.getCallsEventsList(null, null, null, null) } returns resultSuccess

        val result = getCallsEventListUseCase(null, null, null, null)

        assert(result == resultSuccess)
    }

    @Test
    fun `it should return a network error`() = runTest {
        coEvery { repository.getCallsEventsList(null, null, null, null) } returns resultError

        val result = getCallsEventListUseCase(null, null, null, null)

        assert(result == resultError)
    }
}