package br.com.mobicare.cielo.mySales.domain


import br.com.mobicare.cielo.mySales.MySalesFactory.emptyResult
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.getSalesHistoryParams
import br.com.mobicare.cielo.mySales.MySalesFactory.saleHistoryAPISuccess
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesHistoryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetSalesHistoryUseCaseTest {

    private val repository = mockk<MySalesRemoteRepositoryImpl>()
    private lateinit var usecase: GetSalesHistoryUseCase


    @Before
    fun setup() {
        usecase = GetSalesHistoryUseCase(repository)
    }


    @Test
    fun `it should return success for getSummarySalesHistory`() = runBlocking {

        //given
        coEvery { repository.getSummarySalesHistory(getSalesHistoryParams) } returns saleHistoryAPISuccess

        //when
        val result = usecase.invoke(getSalesHistoryParams)

        //then
        assertEquals(saleHistoryAPISuccess,result)

    }

    @Test
    fun `it should return error getSummarySalesHistory`() = runBlocking {

        //given
        coEvery { repository.getSummarySalesHistory(getSalesHistoryParams) } returns genericAPIError

        //when
        val result = usecase.invoke(getSalesHistoryParams)

        //then
        assertEquals(genericAPIError,result)
    }


    @Test
    fun `it should return error empty for  getSummarySalesHistory`() = runBlocking {

        //given
        coEvery { repository.getSummarySalesHistory(getSalesHistoryParams) } returns emptyResult

        //when
        val result = usecase.invoke(getSalesHistoryParams)

        //then
        assertEquals(emptyResult,result)

    }
}