package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackAcceptRepository
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PutChargebackAcceptTest {
    private val repository = mockk<ChargebackAcceptRepository>()

    private val acceptRequest = ChargebackFactory.acceptRequest
    private val acceptResponse = ChargebackFactory.acceptResponse
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(acceptResponse)
    private val putChargebackAccept = PutChargebackAcceptUseCase(repository)

    @Test
    fun `it should call chargeback accept repository only once`() = runBlocking {
        // given
        coEvery { repository.putChargebackAccept(any(), any()) } returns resultSuccess

        // when
        putChargebackAccept(EMPTY, acceptRequest)

        // then
        coVerify(exactly = 1) { repository.putChargebackAccept(any(), any()) }
    }

    @Test
    fun `it should return a chargeback accept response successfully`() = runBlocking {
        // given
        coEvery { repository.putChargebackAccept(any(), any()) } returns resultSuccess

        // when
        val result = putChargebackAccept(EMPTY, acceptRequest)

        // then
        assertEquals(resultSuccess, result)
    }

    @Test
    fun `it should return the correct chargeback accept response`() = runBlocking {
        // given
        coEvery { repository.putChargebackAccept(any(), any()) } returns resultSuccess

        // when
        val result = putChargebackAccept(EMPTY, acceptRequest)

        // then
        assert(result is CieloDataResult.Success)

        val actualResponse = (result as CieloDataResult.Success).value

        assertEquals(actualResponse[0].code, resultSuccess.value[0].code)
        assertEquals(actualResponse[0].message, resultSuccess.value[0].message)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.putChargebackAccept(any(), any()) } returns resultError

        // when
        val result = putChargebackAccept(EMPTY, acceptRequest)

        // then
        assertEquals(resultError, result)
    }
}