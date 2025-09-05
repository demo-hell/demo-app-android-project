package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.GivenUpShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class GivenUpRepositoryTest {
    private val remoteDataSource = mockk<GivenUpShareDataSource>()
    private val repository = GivenUpShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.requestGivenUpShare
    private val successResponse = OpenFinanceFactory.responseGivenUpShare
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with given up share`() =
        runBlocking {

            coEvery { remoteDataSource.givenUpShare(request) } returns resultSuccess

            val result = repository.givenUpShare(request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without given up share`() = runBlocking {

        coEvery { remoteDataSource.givenUpShare(request) } returns OpenFinanceFactory.resultError

        val result = repository.givenUpShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without given up share`() = runBlocking {

        coEvery { remoteDataSource.givenUpShare(request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.givenUpShare(request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}