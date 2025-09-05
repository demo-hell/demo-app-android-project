package br.com.mobicare.cielo.arv.data.repository

import br.com.mobicare.cielo.arv.data.datasource.ArvRemoteDataSource
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ArvRepositoryNewTest {
    private val remoteDataSource = mockk<ArvRemoteDataSource>()

    private val confirmAnticipationResponse = ArvFactory.confirmAnticipationResponse
    private val confirmAnticipationRequest = ArvFactory.confirmAnticipationRequest
    private val arvBankList = ArvFactory.arvBankList
    private val resultError = ArvFactory.resultError
    private val resultInvalidReceivableAmount = ArvFactory.resultAnticipationInvalidReceivableAmount
    private val resultConfirmAnticipationSuccess = CieloDataResult.Success(confirmAnticipationResponse)
    private val resultArvBanksSuccess = CieloDataResult.Success(arvBankList)

    private val repository = ArvRepositoryNewImpl(remoteDataSource)

    @Test
    fun `it should fetch ArvBanks calling remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getBanks() } returns resultArvBanksSuccess

        // when
        repository.getArvBanks()

        // then
        coVerify(exactly = 1) { remoteDataSource.getBanks() }
    }

    @Test
    fun `it should confirm an anticipation calling remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        repository.confirmAnticipation(confirmAnticipationRequest)

        // then
        coVerify(exactly = 1) { remoteDataSource.confirmAnticipation(any()) }
    }

    @Test
    fun `it should fetch ArvBanks successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getBanks() } returns resultArvBanksSuccess

        // when
        val result = repository.getArvBanks()

        // then
        assertEquals(resultArvBanksSuccess, result)
    }

    @Test
    fun `it should confirm an anticipation successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        val result = repository.confirmAnticipation(confirmAnticipationRequest)

        // then
        assertEquals(resultConfirmAnticipationSuccess, result)
    }

    @Test
    fun `it should cancel an scheduled anticipation successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.cancelScheduledAnticipation(any()) } returns CieloDataResult.Empty()

        // when
        val result = repository.cancelScheduledAnticipation(ArvFactory.cancelScheduledAnticipationRequest)

        // then
        assertEquals(CieloDataResult.Empty(), result)
    }

    @Test
    fun `it should return the correct list size of ArvBanks`() = runBlocking {
        // given
        coEvery { remoteDataSource.getBanks() } returns resultArvBanksSuccess

        // when
        val result = repository.getArvBanks()

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)
        assertThat((result as CieloDataResult.Success).value.size)
            .isEqualTo(resultArvBanksSuccess.value.size)
    }

    @Test
    fun `it should return the correct confirm anticipation response`() = runBlocking {
        // given
        coEvery { remoteDataSource.confirmAnticipation(any()) } returns resultConfirmAnticipationSuccess

        // when
        val result = repository.confirmAnticipation(confirmAnticipationRequest)

        // then
        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

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
    fun `it should return a network error when fetching ArvBanks`() = runBlocking {
        // given
        coEvery { remoteDataSource.getBanks() } returns resultError

        // when
        val result = repository.getArvBanks()

        // then
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a network error when confirming an anticipation`() = runBlocking {
        // given
        coEvery { remoteDataSource.confirmAnticipation(any()) } returns resultError

        // when
        val result = repository.confirmAnticipation(confirmAnticipationRequest)

        // then
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a network error when cancel scheduled anticipation`() = runBlocking {
        // given
        coEvery { remoteDataSource.cancelScheduledAnticipation(any()) } returns resultError

        // when
        val result = repository.cancelScheduledAnticipation(ArvFactory.cancelScheduledAnticipationRequest)

        // then
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return an invalid receivable amount when confirming an anticipation`() = runBlocking {
        // given
        coEvery { remoteDataSource.confirmAnticipation(any()) } returns resultInvalidReceivableAmount

        // when
        val result = repository.confirmAnticipation(confirmAnticipationRequest)

        // then
        assertEquals(resultInvalidReceivableAmount, result)
    }
}