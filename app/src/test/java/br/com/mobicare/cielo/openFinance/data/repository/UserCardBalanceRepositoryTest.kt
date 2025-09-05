package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.data.datasource.UserCardBalanceDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UserCardBalanceRepositoryTest {
    private val remoteDataSource = mockk<UserCardBalanceDataSource>()
    private val repository = UserCardBalanceRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successResponseUserCardBalance
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with the total available balance`() =
        runBlocking {

            coEvery { remoteDataSource.getUserCardBalance(EMPTY) } returns resultSuccess

            val result = repository.getUserCardBalance(EMPTY)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without the total available balance`() = runBlocking {

        coEvery { remoteDataSource.getUserCardBalance(EMPTY)} returns OpenFinanceFactory.resultError

        val result = repository.getUserCardBalance(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without the total available balance`() = runBlocking {

        coEvery { remoteDataSource.getUserCardBalance(EMPTY)} returns OpenFinanceFactory.resultEmpty

        val result = repository.getUserCardBalance(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}