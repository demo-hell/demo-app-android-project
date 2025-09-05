package br.com.mobicare.cielo.superlink.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.superlink.domain.repository.SuperLinkRepository
import br.com.mobicare.cielo.superlink.utils.SuperLinkFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CheckPaymentLinkActiveUseCaseTest {
    private val repository = mockk<SuperLinkRepository>()

    private val paymentLinkResponse = SuperLinkFactory.paymentLinkResponseForActiveCheck
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(paymentLinkResponse)
    private val checkPaymentLinkActiveUseCase = CheckPaymentLinkActiveUseCase(repository)

    @Test
    fun `it should call method isPaymentLinkActive of repository only once`() = runBlocking {
        // given
        coEvery { repository.isPaymentLinkActive() } returns resultSuccess

        // when
        checkPaymentLinkActiveUseCase()

        // then
        coVerify(exactly = 1) { repository.isPaymentLinkActive() }
    }

    @Test
    fun `it should return a list of PaymentLinkResponse on CheckPaymentLinkActiveUseCase call successfully`() = runBlocking {
        // given
        coEvery { repository.isPaymentLinkActive() } returns resultSuccess

        // when
        val result = checkPaymentLinkActiveUseCase()

        // then
        assertEquals(resultSuccess, result)
    }

    @Test
    fun `it should return the correct list size of PaymentLinkResponse on CheckPaymentLinkActiveUseCase call`() = runBlocking {
        // given
        coEvery { repository.isPaymentLinkActive() } returns resultSuccess

        // when
        val result = checkPaymentLinkActiveUseCase()

        // then
        assertEquals(resultSuccess, result)

        (result as CieloDataResult.Success).value.run {
            assertEquals(resultSuccess.value.items?.size, items?.size)
        }
    }

    @Test
    fun `it should return a network error on CheckPaymentLinkActiveUseCase call`() = runBlocking {
        // given
        coEvery { repository.isPaymentLinkActive() } returns resultError

        // when
        val result = repository.isPaymentLinkActive()

        // then
        assertEquals(result, resultError)
    }
}