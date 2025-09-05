package br.com.mobicare.cielo.mySales.data

import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.MySalesFactory.filterCardBrandAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.filteredPaymentTypesAndCanceledSellsAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericSalesAndCardBrandFiltersParams
import br.com.mobicare.cielo.mySales.data.datasource.remote.MySalesFiltersRemoteDataSource
import br.com.mobicare.cielo.mySales.data.repository.MySalesFiltersRemoteRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class MySalesFiltersRemoteRepositoryTest {

    private val remoteDataSource = mockk<MySalesFiltersRemoteDataSource>()
    private lateinit var repository: MySalesFiltersRemoteRepositoryImpl
    private val resultError = MySalesFactory.genericAPIError


    @Before
    fun setup() {
        repository = MySalesFiltersRemoteRepositoryImpl(remoteDataSource)
    }


    //region - getFilteredCardBrands tests
    @Test
    fun `api call should return success for getFilteredCardBrands`() = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams) } returns filterCardBrandAPISuccess

        //when
        val result = repository.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(filterCardBrandAPISuccess,result)

    }

    @Test
    fun `api call should return error for  getFilteredCardBrands`() = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams) } returns resultError

        //when
        val result = repository.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - getFilteredPaymentTypes
    @Test
    fun `api call should return success for getFilteredPaymentTypes`() = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams) } returns filteredPaymentTypesAndCanceledSellsAPISuccess

        //when
        val result = repository.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(result,filteredPaymentTypesAndCanceledSellsAPISuccess)

    }


    @Test
    fun `api call should return error for  getFilteredPaymentTypes`() = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams) } returns resultError

        //when
        val result = repository.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion


    //region - getFilteredCanceledSells

    @Test
    fun `api call should return success for getFilteredCanceledSells`()  = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams) } returns filteredPaymentTypesAndCanceledSellsAPISuccess

        //when
        val result = repository.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(filteredPaymentTypesAndCanceledSellsAPISuccess,result)
    }



    @Test
    fun `api call should return error for  getFilteredCanceledSells`() = runBlocking {

        //given
        coEvery { remoteDataSource.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams) } returns resultError

        //when
        val result = repository.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(resultError,result)
    }

    //endregion

}