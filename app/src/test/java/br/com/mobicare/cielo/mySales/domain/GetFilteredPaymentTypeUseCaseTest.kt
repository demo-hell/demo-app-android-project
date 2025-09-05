package br.com.mobicare.cielo.mySales.domain


import br.com.mobicare.cielo.mySales.MySalesFactory.filterCardBrandAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.filteredPaymentTypesAndCanceledSellsAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.genericSalesAndCardBrandFiltersParams
import br.com.mobicare.cielo.mySales.data.repository.MySalesFiltersRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetCardBrandsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetPaymentTypeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetFilteredPaymentTypeUseCaseTest {

    private val repository = mockk<MySalesFiltersRemoteRepositoryImpl>()
    private lateinit var usecase: GetPaymentTypeUseCase


    @Before
    fun setup() {
        usecase = GetPaymentTypeUseCase(repository)
    }

    @Test
    fun `it should return success for getPaymentTypeUseCase`() = runBlocking {

        //given
        coEvery { repository.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams) } returns filteredPaymentTypesAndCanceledSellsAPISuccess

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(filteredPaymentTypesAndCanceledSellsAPISuccess,result)
    }


    @Test
    fun `it should return error for getPaymentTypeUseCase`() = runBlocking {

        //given
        coEvery { repository.getFilteredPaymentTypes(genericSalesAndCardBrandFiltersParams) } returns genericAPIError

        //when
        val result = usecase.invoke(genericSalesAndCardBrandFiltersParams)

        //then
        assertEquals(genericAPIError,result)

    }
}