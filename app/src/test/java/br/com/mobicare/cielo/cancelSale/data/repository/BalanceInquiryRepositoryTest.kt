package br.com.mobicare.cielo.cancelSale.data.repository

import br.com.mobicare.cielo.cancelSale.data.datasource.BalanceInquiryDataSource
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class BalanceInquiryRepositoryTest {
    private val remoteDataSource = mockk<BalanceInquiryDataSource>()
    private val repository = BalanceInquiryRepositoryImpl(remoteDataSource)
    private val request = CancelSaleFactory.requestBalanceInquiry
    private val successResponse = CancelSaleFactory.responseBalanceInquiry
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with balance inquiry`() =
        runBlocking {

            coEvery { remoteDataSource.balanceInquiry(request) } returns resultSuccess

            val result = repository.balanceInquiry(request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without balance inquiry`() = runBlocking {

        coEvery { remoteDataSource.balanceInquiry(request) } returns CancelSaleFactory.resultError

        val result = repository.balanceInquiry(request)

        Assert.assertEquals(CancelSaleFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without balance inquiry`() = runBlocking {

        coEvery { remoteDataSource.balanceInquiry(request) } returns CancelSaleFactory.resultEmpty

        val result = repository.balanceInquiry(request)

        Assert.assertEquals(CancelSaleFactory.resultEmpty, result)
    }
}