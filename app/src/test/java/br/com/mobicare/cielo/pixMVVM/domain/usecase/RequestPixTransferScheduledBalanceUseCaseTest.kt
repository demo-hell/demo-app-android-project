package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class RequestPixTransferScheduledBalanceUseCaseTest {
    private val repository = mockk<PixTransactionsRepository>()

    private val params = RequestPixTransferScheduledBalanceUseCase.Params(EMPTY)

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(Unit)

    private val requestPixTransferScheduledBalanceUseCase = RequestPixTransferScheduledBalanceUseCase(repository)

    @Test
    fun `it should call method transferScheduledBalance of repository only once`() =
        runBlocking {
            // given
            coEvery { repository.transferScheduledBalance(any()) } returns successResult

            // when
            requestPixTransferScheduledBalanceUseCase(params)

            // then
            coVerify(exactly = 1) { repository.transferScheduledBalance(any()) }
        }

    @Test
    fun `it should return a success result`() =
        runBlocking {
            // given
            coEvery { repository.transferScheduledBalance(any()) } returns successResult

            // when
            val result = requestPixTransferScheduledBalanceUseCase(params)

            // then
            assertEquals(successResult, result)
        }

    @Test
    fun `it should return a network error`() =
        runBlocking {
            // given
            coEvery { repository.transferScheduledBalance(any()) } returns errorResult

            // when
            val result = requestPixTransferScheduledBalanceUseCase(params)

            // then
            assertEquals(errorResult, result)
        }
}
