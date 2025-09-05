package br.com.mobicare.cielo.mySales.domain

import br.com.mobicare.cielo.mySales.MySalesFactory.genericSalesAndCardBrandFiltersParams
import br.com.mobicare.cielo.mySales.MySalesFactory.filteredPaymentTypesAndCanceledSellsAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.data.repository.MySalesFiltersRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetFilteredCanceledSellsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetFilteredCanceledSellsUseCaseTest {

    private val repository = mockk<MySalesFiltersRemoteRepositoryImpl>()
    private lateinit var usecase: GetFilteredCanceledSellsUseCase


    @Before
    fun setup() {
        usecase = GetFilteredCanceledSellsUseCase(repository)
    }


    @Test
    fun `it should return a success for getFilteredCanceledSellsUseCase`() = runBlocking {

        //given
        coEvery { repository.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams) } returns filteredPaymentTypesAndCanceledSellsAPISuccess

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(filteredPaymentTypesAndCanceledSellsAPISuccess,result)
    }

    @Test
    fun `it should return error for getFilteredCanceledSellsUseCase`() = runBlocking {

        //given
        coEvery { repository.getFilteredCanceledSells(genericSalesAndCardBrandFiltersParams)  } returns genericAPIError

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(genericAPIError,result)
    }




}