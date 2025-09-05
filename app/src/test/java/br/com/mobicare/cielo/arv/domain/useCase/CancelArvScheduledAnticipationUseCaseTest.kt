package br.com.mobicare.cielo.arv.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CancelArvScheduledAnticipationUseCaseTest {
    private val repository = mockk<ArvRepositoryNew>()

    private val cancelArvScheduledAnticipationRequest = ArvFactory.cancelScheduledAnticipationRequest
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultCancelAnticipationSuccess = CieloDataResult.Empty()
    private val cancelArvScheduledAnticipationUseCase = CancelArvScheduledAnticipationUseCase(repository)

    @Test
    fun `it should call canncel anticipation repository only once`() = runBlocking {
        // given
        coEvery { repository.cancelScheduledAnticipation(any()) } returns resultCancelAnticipationSuccess

        // when
        cancelArvScheduledAnticipationUseCase(cancelArvScheduledAnticipationRequest)

        // then
        coVerify(exactly = 1) { repository.cancelScheduledAnticipation(any()) }
    }

    @Test
    fun `it should return a cancel anticipation response successfully`() = runBlocking {
        // given
        coEvery { repository.cancelScheduledAnticipation(any()) } returns resultCancelAnticipationSuccess

        // when
        val result = cancelArvScheduledAnticipationUseCase(cancelArvScheduledAnticipationRequest)

        // then
        assertEquals(resultCancelAnticipationSuccess, result)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.cancelScheduledAnticipation(any()) } returns resultError

        // when
        val result = cancelArvScheduledAnticipationUseCase(cancelArvScheduledAnticipationRequest)

        // then
        assertEquals(resultError, result)
    }
}