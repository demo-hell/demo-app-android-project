package br.com.mobicare.cielo.chargeback.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
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

class GetChargebackLifecycleTest {
    private val repository = mockk<ChargebackRepository>()

    private val lifecycleList = ChargebackFactory.lifecycleList
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(lifecycleList)
    private val getChargebackLifecycle = GetChargebackLifecycleUseCase(repository)

    @Test
    fun `it should call chargeback repository only once`() = runBlocking {
        // given
        coEvery { repository.getChargebackLifecycle(any()) } returns resultSuccess

        // when
        getChargebackLifecycle(ZERO)

        // then
        coVerify(exactly = 1) { repository.getChargebackLifecycle(any()) }
    }

    @Test
    fun `it should return a list of chargeback lifecycles successfully`() = runBlocking {
        // given
        coEvery { repository.getChargebackLifecycle(any()) } returns resultSuccess

        // when
        val result = getChargebackLifecycle(ZERO)

        // then
        assertEquals(result, resultSuccess)
    }

    @Test
    fun `it should return the correct list size of chargeback lifecycles`() = runBlocking {
        // given
        coEvery { repository.getChargebackLifecycle(any()) } returns resultSuccess

        // when
        val result = getChargebackLifecycle(ZERO) as CieloDataResult.Success

        // then
        assertEquals(result.value.size, lifecycleList.size)
    }

    @Test
    fun `it should return a network error`() = runBlocking {
        // given
        coEvery { repository.getChargebackLifecycle(any()) } returns resultError

        // when
        val result = getChargebackLifecycle(ZERO)

        // then
        assertEquals(result, resultError)
    }
}