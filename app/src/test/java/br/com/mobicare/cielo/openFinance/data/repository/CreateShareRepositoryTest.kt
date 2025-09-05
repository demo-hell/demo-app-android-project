package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.CreateShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class CreateShareRepositoryTest {
    private val remoteDataSource = mockk<CreateShareDataSource>()
    private val repository = CreateShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.requestCreateShare
    private val successResponse = OpenFinanceFactory.successCreateShare
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with data for create sharing`() =
        runBlocking {

            coEvery { remoteDataSource.createShare(request) } returns resultSuccess

            val result = repository.createShare(request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without data for create sharing`() = runBlocking {

        coEvery { remoteDataSource.createShare(request) } returns OpenFinanceFactory.resultError

        val result = repository.createShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without data for create sharing`() = runBlocking {

        coEvery { remoteDataSource.createShare(request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.createShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}