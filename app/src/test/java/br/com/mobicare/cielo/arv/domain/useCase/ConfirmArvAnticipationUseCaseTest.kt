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

class ConfirmArvAnticipationUseCaseTest {
    private val repository = mockk<ArvRepositoryNew>()

    private val confirmAnticipationResponse = ArvFactory.confirmAnticipationResponse
    private val confirmAnticipationRequest = ArvFactory.confirmAnticipationRequest
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultInvalidReceivableAmount = ArvFactory.resultAnticipationInvalidReceivableAmount
    private val resultConfirmAnticipationSuccess = CieloDataResult.Success(confirmAnticipationResponse)
    private val confirmAnticipationUseCase = ConfirmArvAnticipationUseCase(repository)

    @Test
    fun `it should call confirm anticipation repository only once`() = runBlocking {
        // given
        coEvery { repository.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        confirmAnticipationUseCase(confirmAnticipationRequest)

        // then
        coVerify(exactly = 1) { repository.confirmAnticipation(any()) }
    }

    @Test
    fun `it should return a confirm anticipation response successfully`() = runBlocking {
        // given
        coEvery { repository.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        val result = confirmAnticipationUseCase(confirmAnticipationRequest)

        // then
        assertEquals(resultConfirmAnticipationSuccess, result)
    }

    @Test
    fun `it should return the correct confirm anticipation response`() = runBlocking {
        // given
        coEvery { repository.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        val result = confirmAnticipationUseCase(confirmAnticipationRequest)

        // then
        assert(result is CieloDataResult.Success)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value
        val expectedData = resultConfirmAnticipationSuccess.value

        assertEquals(expectedData.discountAmount, actualData.discountAmount)
        assertEquals(expectedData.grossAmount, actualData.grossAmount)
        assertEquals(expectedData.discountAmount, actualData.discountAmount)
        assertEquals(expectedData.netAmount, actualData.netAmount)
        assertEquals(expectedData.modality, actualData.modality)
        assertEquals(expectedData.negotiationDate, actualData.negotiationDate)
        assertEquals(expectedData.negotiationFee, actualData.negotiationFee)
        assertEquals(expectedData.negotiationType, actualData.negotiationType)
        assertEquals(expectedData.operationNumber, actualData.operationNumber)
        assertEquals(expectedData.status, actualData.status)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.confirmAnticipation(any()) } returns resultError

        // when
        val result = confirmAnticipationUseCase(confirmAnticipationRequest)

        // then
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return an invalid receivable amount`() = runBlocking {
        // given
        coEvery { repository.confirmAnticipation(any()) } returns resultInvalidReceivableAmount

        // when
        val result = confirmAnticipationUseCase(confirmAnticipationRequest)

        // then
        assertEquals(resultInvalidReceivableAmount, result)
    }
}