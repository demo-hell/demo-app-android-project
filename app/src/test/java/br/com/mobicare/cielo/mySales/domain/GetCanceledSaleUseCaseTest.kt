package br.com.mobicare.cielo.mySales.domain

import br.com.mobicare.cielo.mySales.MySalesFactory.canceledSaleParams
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.genericCanceledAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericEmptySummarySalesBO
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetCanceledSalesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCanceledSaleUseCaseTest {

    private val repository = mockk<MySalesRemoteRepositoryImpl>()
    private lateinit var usecase: GetCanceledSalesUseCase


    @Before
    fun setup() {
        usecase = GetCanceledSalesUseCase(repository)
    }


    @Test
    fun `it should return a success canceled sales summary`() = runBlocking {

        //given
        coEvery { repository.getCanceledSales(canceledSaleParams) } returns genericCanceledAPISuccess

        //when
        val result = usecase.invoke(canceledSaleParams)

        //then
        assertEquals(genericCanceledAPISuccess, result)
    }


    @Test
    fun `it should return error`() = runBlocking {

        //given
        coEvery { repository.getCanceledSales(canceledSaleParams) } returns genericAPIError

        //when
        val result = usecase.invoke(canceledSaleParams)

        //then
        assertEquals(genericAPIError,result)
    }


    @Test
    fun `it should return empty canceled sales`() = runBlocking {

        //given
        coEvery { repository.getCanceledSales(canceledSaleParams) } returns genericEmptySummarySalesBO

        //when
        val result = usecase.invoke(canceledSaleParams)

        //then
        assertEquals(genericEmptySummarySalesBO, result)
    }
}