package br.com.mobicare.cielo.mySales.domain

import br.com.mobicare.cielo.mySales.MySalesFactory.filterCardBrandAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.genericSalesAndCardBrandFiltersParams
import br.com.mobicare.cielo.mySales.data.repository.MySalesFiltersRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetCardBrandsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetFilteredCardBrandUseCaseTest {

    private val repository = mockk<MySalesFiltersRemoteRepositoryImpl>()
    private lateinit var usecase: GetCardBrandsUseCase


    @Before
    fun setup() {
        usecase = GetCardBrandsUseCase(repository)
    }

    @Test
    fun `it should return a success for getFilteredCardBrands `() = runBlocking {

        //given
        coEvery { repository.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams) } returns filterCardBrandAPISuccess

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(filterCardBrandAPISuccess,result)

    }


    @Test
    fun `it should return error for getFilteredCardBrands`() = runBlocking {

        //given
        coEvery { repository.getFilteredCardBrands(genericSalesAndCardBrandFiltersParams) } returns genericAPIError

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(genericAPIError,result)

    }




}