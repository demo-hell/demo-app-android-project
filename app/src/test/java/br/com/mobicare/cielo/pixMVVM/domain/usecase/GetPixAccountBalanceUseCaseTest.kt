package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAccountBalanceRepository
import br.com.mobicare.cielo.pixMVVM.utils.PixAccountBalanceFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPixAccountBalanceUseCaseTest {
    private val repository = mockk<PixAccountBalanceRepository>()

    private val pixAccountBalance = PixAccountBalanceFactory.pixAccountBalanceEntity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(pixAccountBalance)
    private val getPixAccountBalanceUseCase = GetPixAccountBalanceUseCase(repository)

    @Test
    fun `it should call method getAccountBalance of repository only once`() = runBlocking {
        // given
        coEvery { repository.getAccountBalance() } returns successResult

        // when
        getPixAccountBalanceUseCase()

        // then
        coVerify(exactly = 1) { repository.getAccountBalance() }
    }

    @Test
    fun `it should return the correct PixAccountBalance entity on getPixAccountBalanceUseCase call successfully`() = runBlocking {
        // given
        coEvery { repository.getAccountBalance() } returns successResult

        // when
        val result = getPixAccountBalanceUseCase()

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = pixAccountBalance

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getPixAccountBalanceUseCase call`() = runBlocking {
        // given
        coEvery { repository.getAccountBalance() } returns errorResult

        // when
        val result = getPixAccountBalanceUseCase()

        // then
        assertEquals(errorResult, result)
    }
}