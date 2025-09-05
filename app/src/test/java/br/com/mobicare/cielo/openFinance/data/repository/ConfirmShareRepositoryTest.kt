package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ConfirmShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConfirmShareRepositoryTest {
    private val remoteDataSource = mockk<ConfirmShareDataSource>()
    private val repository = ConfirmShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.requestConfirmShare
    private val successResponse = OpenFinanceFactory.responseConfirmShare
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with confirm share`() =
        runBlocking {

            coEvery { remoteDataSource.confirmShare(request) } returns resultSuccess

            val result = repository.confirmShare(request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without confirm share`() = runBlocking {

        coEvery { remoteDataSource.confirmShare(request) } returns OpenFinanceFactory.resultError

        val result = repository.confirmShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without confirm share`() = runBlocking {

        coEvery { remoteDataSource.confirmShare(request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.confirmShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}