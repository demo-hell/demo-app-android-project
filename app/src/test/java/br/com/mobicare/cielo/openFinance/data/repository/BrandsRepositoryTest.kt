package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.data.datasource.BrandsDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class BrandsRepositoryTest {
    private val remoteDataSource = mockk<BrandsDataSource>()
    private val repository = BrandsRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successBrands
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with brands`() =
        runBlocking {

            coEvery { remoteDataSource.getBrands(EMPTY) } returns resultSuccess

            val result = repository.getBrands(EMPTY)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without brands`() = runBlocking {

        coEvery { remoteDataSource.getBrands(EMPTY)} returns OpenFinanceFactory.resultError

        val result = repository.getBrands(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without brands`() = runBlocking {

        coEvery { remoteDataSource.getBrands(EMPTY)} returns OpenFinanceFactory.resultEmpty

        val result = repository.getBrands(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}