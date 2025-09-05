package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ChangeOrRenewShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ChangeOrRenewShareRepositoryTest {
    private val remoteDataSource = mockk<ChangeOrRenewShareDataSource>()
    private val repository = ChangeOrRenewShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.requestChangeOrRenewShare
    private val response = OpenFinanceFactory.responseChangeOrRenewShare
    private val resultSuccess = CieloDataResult.Success(response)

    @Test
    fun `it should return a successful response if change or renew share is correct`() =
        runBlocking {

            coEvery { remoteDataSource.changeOrRenewShare(request) } returns resultSuccess

            val result = repository.changeOrRenewShare(request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if change or renew share is incorrect`() = runBlocking {

        coEvery { remoteDataSource.changeOrRenewShare(request) } returns OpenFinanceFactory.resultError

        val result = repository.changeOrRenewShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if change or renew share is empty`() = runBlocking {

        coEvery { remoteDataSource.changeOrRenewShare(request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.changeOrRenewShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}