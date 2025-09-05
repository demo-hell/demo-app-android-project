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
import retrofit2.Response

class ConfirmArvScheduledAnticipationUseCaseTest {
    private val repository = mockk<ArvRepositoryNew>()

    private val confirmScheduledAnticipationRequest = ArvFactory.confirmScheduledAnticipationRequest
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val confirmScheduledAnticipationUseCase = ConfirmArvScheduledAnticipationUseCase(repository)


    @Test
    fun `it should call confirm scheduled anticipation repository only once`() = runBlocking {
        // given
        coEvery { repository.confirmScheduledAnticipation(any()) } returns CieloDataResult.Empty()

        // when
        confirmScheduledAnticipationUseCase( confirmScheduledAnticipationRequest)

        // then
        coVerify(exactly = 1) { repository.confirmScheduledAnticipation(any()) }
    }

    @Test
    fun `it should return a confirm scheduled anticipation response successfully`() = runBlocking {
        // given
        coEvery { repository.confirmScheduledAnticipation(any()) } returns CieloDataResult.Empty()

        // when
        val result = confirmScheduledAnticipationUseCase(confirmScheduledAnticipationRequest)

        // then
        assertEquals(CieloDataResult.Empty(), result)
    }


    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.confirmScheduledAnticipation(any()) } returns resultError

        // when
        val result = confirmScheduledAnticipationUseCase(confirmScheduledAnticipationRequest)

        // then
        assertEquals(resultError, result)
    }
}