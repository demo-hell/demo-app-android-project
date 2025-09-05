package br.com.mobicare.cielo.mySales.domain


import br.com.mobicare.cielo.mySales.MySalesFactory.emptyResult
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.mySalesHomeParams
import br.com.mobicare.cielo.mySales.MySalesFactory.summarySalesAPISuccess
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test



class GetSaleUseCaseTest {

    private val repository = mockk<MySalesRemoteRepositoryImpl>()
    private lateinit var usecase: GetSalesUseCase


    @Before
    fun setup() {
        usecase = GetSalesUseCase(repository)
    }


    @Test
    fun `it should return success for getSummarySales`() = runBlocking {

        //given
        coEvery { repository.getSummarySales( mySalesHomeParams ) } returns summarySalesAPISuccess

        //when
        val result = usecase.invoke(mySalesHomeParams)

        //then
        assertEquals(summarySalesAPISuccess,result)

    }


    @Test
    fun `it should return error for getSummarySales`() = runBlocking {

        //given
        coEvery { repository.getSummarySales(mySalesHomeParams) } returns genericAPIError

        //when
        val result = usecase.invoke(mySalesHomeParams)

        //then
        assertEquals(genericAPIError,result)

    }

    @Test
    fun `it should return a empty state`() = runBlocking {

        //given
        coEvery { repository.getSummarySales(mySalesHomeParams) } returns emptyResult

        //when
        val result = usecase.invoke(mySalesHomeParams)

        //then
        assertEquals(emptyResult,result)
    }





}