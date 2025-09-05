package br.com.mobicare.cielo.mySales.domain


import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.getSaleMerchantParams
import br.com.mobicare.cielo.mySales.MySalesFactory.saleMerchantAPISuccess
import br.com.mobicare.cielo.mySales.data.repository.MySalesRemoteRepositoryImpl
import br.com.mobicare.cielo.mySales.domain.usecase.GetSaleMerchantUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class GetSalesMerchantUseCaseTest {

    private val repository = mockk<MySalesRemoteRepositoryImpl>()
    private lateinit var usecase: GetSaleMerchantUseCase


    @Before
    fun setup() {
        usecase = GetSaleMerchantUseCase(repository)
    }


    @Test
    fun `it should return success for getSaleMerchant`() = runBlocking {

        //given
        coEvery { repository.getSaleMerchant(getSaleMerchantParams) } returns saleMerchantAPISuccess

        //when
        val result = usecase.invoke(getSaleMerchantParams)

        //then
        assertEquals(saleMerchantAPISuccess,result)

    }


    @Test
    fun `it should return error`() = runBlocking {
        //given
        coEvery { repository.getSaleMerchant(getSaleMerchantParams) } returns genericAPIError

        //when
        val result = usecase.invoke(getSaleMerchantParams)

        //then
        assertEquals(genericAPIError,result)
    }

}