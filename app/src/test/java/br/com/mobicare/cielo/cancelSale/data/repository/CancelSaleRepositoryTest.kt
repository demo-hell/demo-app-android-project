package br.com.mobicare.cielo.cancelSale.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.cancelSale.data.datasource.CancelSaleDataSource
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class CancelSaleRepositoryTest {
    private val remoteDataSource = mockk<CancelSaleDataSource>()
    private val repository = CancelSaleRepositoryImpl(remoteDataSource)
    private val request = CancelSaleFactory.cancelSaleRequest
    private val successResponse = CancelSaleFactory.cancelSaleResponse
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with message of sale cancelled`() =
        runBlocking {

            coEvery { remoteDataSource.cancelSale(EMPTY, arrayListOf(request)) } returns resultSuccess

            val result = repository.cancelSale(EMPTY, arrayListOf(request))

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without message of sale cancelled`() = runBlocking {

        coEvery { remoteDataSource.cancelSale(EMPTY, arrayListOf(request)) } returns CancelSaleFactory.resultError

        val result = repository.cancelSale(EMPTY, arrayListOf(request))

        Assert.assertEquals(CancelSaleFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without message of sale cancelled`() = runBlocking {

        coEvery { remoteDataSource.cancelSale(EMPTY, arrayListOf(request)) } returns CancelSaleFactory.resultEmpty

        val result = repository.cancelSale(EMPTY, arrayListOf(request))

        Assert.assertEquals(CancelSaleFactory.resultEmpty, result)
    }
}