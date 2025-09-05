package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.PixMerchantDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.resultError
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.resultErrorNotFound
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class PixMerchantRepositoryTest {
    private val remoteDataSource = mockk<PixMerchantDataSource>()
    private val repository = PixMerchantRemoteRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successResponsePixMerchant
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return response successfully if has open finance account pix`() = runBlocking {

        coEvery { remoteDataSource.getPixMerchantListOpenFinance() } returns resultSuccess

        val result = repository.getPixMerchantListOpenFinance()

        Assert.assertEquals(resultSuccess, result)
    }

    @Test
    fun `it should return response error if has a error in service open finance account pix`() = runBlocking {

        coEvery { remoteDataSource.getPixMerchantListOpenFinance() } returns resultError

        val result = repository.getPixMerchantListOpenFinance()

        Assert.assertEquals(resultError, result)
    }

    @Test
    fun `it should return a response error not found if don't has account pix open finance`() = runBlocking {

        coEvery { remoteDataSource.getPixMerchantListOpenFinance() } returns resultErrorNotFound

        val result = repository.getPixMerchantListOpenFinance()

        Assert.assertEquals(resultErrorNotFound, result)
    }
}