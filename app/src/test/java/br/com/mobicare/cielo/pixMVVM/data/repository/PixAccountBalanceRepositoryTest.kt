package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.asSuccess
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAccountBalanceRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.utils.PixAccountBalanceFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PixAccountBalanceRepositoryTest {
    private val remoteDataSource = mockk<PixAccountBalanceRemoteDataSource>()

    private val pixAccountBalance = PixAccountBalanceFactory.pixAccountBalanceEntity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(pixAccountBalance)

    private val repository = PixAccountBalanceRepositoryImpl(remoteDataSource)

    @Test
    fun `it should call method getAccountBalance of remote data source only once`() = runBlocking {
        // given
        coEvery { remoteDataSource.getAccountBalance() } returns successResult

        // when
        repository.getAccountBalance()

        // then
        coVerify(exactly = 1) { remoteDataSource.getAccountBalance() }
    }

    @Test
    fun `it should return the correct PixAccountBalance entity on getAccountBalance call successfully`() = runBlocking {
        // given
        coEvery { remoteDataSource.getAccountBalance() } returns successResult

        // when
        val result = repository.getAccountBalance()

        // then
        assertEquals(successResult, result)

        val actual = result.asSuccess.value
        val expected = pixAccountBalance

        assertEquals(expected, actual)
    }

    @Test
    fun `it should return a network error on getAccountBalance call`() = runBlocking {
        // given
        coEvery { remoteDataSource.getAccountBalance() } returns errorResult

        // when
        val result = repository.getAccountBalance()

        // then
        assertEquals(errorResult, result)
    }

}