package br.com.mobicare.cielo.mySales.data


import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesRemoteDataSource
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class MySalesRemoteRepositoryTest {

    private val remoteDataSource = mockk<MySalesRemoteDataSource>()
    private lateinit var repository: MySalesRemoteRepositoryImpl
    private val resultError = MySalesFactory.genericAPIError

    //region - Params
    private val summarySalesParams = MySalesFactory.mySalesHomeParams
    private val canceledSalesParams = MySalesFactory.canceledSaleParams
    private val historySalesParams = MySalesFactory.getSalesHistoryParams
    private val merchantSaleParams = MySalesFactory.getSaleMerchantParams
    //endregion


    //region - Success results
    private val getSummarySalesResultSuccess = MySalesFactory.summarySalesAPISuccess
    private val getCanceledSalesResultSuccess = MySalesFactory.genericCanceledAPISuccess
    private val getSummarySalesHistoryResultSuccess = MySalesFactory.saleHistoryAPISuccess
    private val getSaleMerchantAPISuccess = MySalesFactory.saleMerchantAPISuccess
    //endregion


    @Before
    fun setup() {
        repository = MySalesRemoteRepositoryImpl(remoteDataSource)
    }


    //region - getSummarySales tests
    @Test
    fun `api call should return success for getSummarySales`() = runBlocking {

        //given
        coEvery { remoteDataSource.getSummarySales(summarySalesParams) } returns getSummarySalesResultSuccess

        //when
        val result = repository.getSummarySales(summarySalesParams)

        //then
        assertEquals(getSummarySalesResultSuccess,result)

    }

    @Test
    fun `api call should return error for getSummarySales`() = runBlocking {
        //given
        coEvery { remoteDataSource.getSummarySales(summarySalesParams) } returns resultError

        //when
        val result = repository.getSummarySales(summarySalesParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - getMySalesTransactions Tests
    @Test
    fun `api call should return success for getMySalesTransactions`() = runBlocking {

        //given
        coEvery { remoteDataSource.getMySalesTransactions(summarySalesParams) } returns getSummarySalesResultSuccess

        //when
        val result = repository.getMySalesTransactions(summarySalesParams)

        //then
        assertEquals(getSummarySalesResultSuccess,result)

    }

    @Test
    fun `api call should return error for getMySalesTransactions`() = runBlocking {
        //given
        coEvery { remoteDataSource.getMySalesTransactions(summarySalesParams) } returns resultError

        //when
        val result = repository.getMySalesTransactions(summarySalesParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - getCanceledSales tests
    @Test
    fun `api call shold return success for getCanceledSales`() = runBlocking {

        //given
        coEvery { remoteDataSource.getCanceledSales(canceledSalesParams) } returns getCanceledSalesResultSuccess

        //when
        val result = repository.getCanceledSales(canceledSalesParams)

        //then
        assertEquals(getCanceledSalesResultSuccess,result)
    }

    @Test
    fun `api call should return error for getCanceledSales`() = runBlocking {
        //given
        coEvery { remoteDataSource.getCanceledSales(canceledSalesParams) } returns resultError

        //when
        val result = repository.getCanceledSales(canceledSalesParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - SummarySalesHistory tests
    @Test
    fun `api call should return success for getSummarySalesHistory`() = runBlocking {

        //given
        coEvery { remoteDataSource.getSummarySalesHistory(historySalesParams) } returns getSummarySalesHistoryResultSuccess

        //when
        val result = repository.getSummarySalesHistory(historySalesParams)

        //then
        assertEquals(getSummarySalesHistoryResultSuccess,result)
    }


    @Test
    fun `api call should return error for getSummarySalesHistory`() = runBlocking {
        //given
        coEvery { remoteDataSource.getSummarySalesHistory(historySalesParams) } returns resultError

        //when
        val result = repository.getSummarySalesHistory(historySalesParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - SalesMerchant Tests
    @Test
    fun `api call should return success for getSaleMerchant`() = runBlocking {

        //given
        coEvery { remoteDataSource.getSaleMerchant(merchantSaleParams) } returns getSaleMerchantAPISuccess

        //when
        val result = repository.getSaleMerchant(merchantSaleParams)

        //then
        assertEquals(getSaleMerchantAPISuccess, result)
    }

    @Test
    fun `api call should return error for getSaleMerchant`() = runBlocking {
        //given
        coEvery { remoteDataSource.getSaleMerchant(merchantSaleParams) } returns resultError

        //when
        val result = repository.getSaleMerchant(merchantSaleParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion
}