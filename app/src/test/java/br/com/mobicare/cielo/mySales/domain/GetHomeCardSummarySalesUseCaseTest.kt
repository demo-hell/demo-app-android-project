package br.com.mobicare.cielo.mySales.domain

import br.com.mobicare.cielo.mySales.domain.usecase.GetHomeCardSummarySalesUseCase
import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

class GetHomeCardSummarySalesUseCaseTest {

    private val repository = mockk<MySalesRemoteRepositoryImpl>()

    private val params = MySalesFactory.mySalesHomeParams
    private val apiResultError = MySalesFactory.genericAPIError
    private val apiResultSuccess = MySalesFactory.summarySalesAPISuccess
    private val useCaseResultSuccess = MySalesFactory.homeCardSummaryAPISuccess
    private lateinit var usecase: GetHomeCardSummarySalesUseCase


    @Before
    fun setup() {
        usecase = GetHomeCardSummarySalesUseCase(repository)
    }



    @Test
    fun `it should return a success home card`() = runBlocking {
        //given
        coEvery { repository.getSummarySales(params) } returns apiResultSuccess

        //when
        val result = usecase.invoke(params)

        //then
        assertEquals(useCaseResultSuccess,result)
    }


    @Test
    fun `it should return error`() = runBlocking {
        //given
        coEvery { repository.getSummarySales(params) } returns apiResultError

        //when
        val result = usecase.invoke(params)

        //then
        assertEquals(apiResultError,result)
    }

}