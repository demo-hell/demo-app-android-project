package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.TermsOfUseDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.responseTermsOfUse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class TermsOfUseRepositoryTest {
    private val remoteDataSource = mockk<TermsOfUseDataSource>()
    private val repository = TermsOfUseRepositoryImpl(remoteDataSource)
    private val successResponse = responseTermsOfUse
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with base64 terms of use`() =
        runBlocking {

            coEvery { remoteDataSource.getTermsOfUse() } returns resultSuccess

            val result = repository.getTermsOfUse()

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without base64 terms of use`() = runBlocking {

        coEvery { remoteDataSource.getTermsOfUse() } returns OpenFinanceFactory.resultError

        val result = repository.getTermsOfUse()

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without base64 terms of use`() = runBlocking {

        coEvery { remoteDataSource.getTermsOfUse() } returns OpenFinanceFactory.resultEmpty

        val result = repository.getTermsOfUse()

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}