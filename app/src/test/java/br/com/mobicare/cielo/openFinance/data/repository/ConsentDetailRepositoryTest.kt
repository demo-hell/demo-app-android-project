package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.ConsentDetailDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConsentDetailRepositoryTest {
    private val remoteDataSource = mockk<ConsentDetailDataSource>()
    private val repository = ConsentDetailRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successConsentDetail
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response if consent detail is correct`() =
        runBlocking {

            coEvery {
                remoteDataSource.getConsentDetail(OpenFinanceFactory.consentId)
            } returns resultSuccess

            val result = repository.getConsentDetail(OpenFinanceFactory.consentId)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if consent detail is incorrect`() = runBlocking {

        coEvery {
            remoteDataSource.getConsentDetail(OpenFinanceFactory.consentId)
        } returns OpenFinanceFactory.resultError

        val result = repository.getConsentDetail(OpenFinanceFactory.consentId)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if consent detail is empty`() = runBlocking {

        coEvery {
            remoteDataSource.getConsentDetail(OpenFinanceFactory.consentId)
        } returns OpenFinanceFactory.resultEmpty

        val result = repository.getConsentDetail(OpenFinanceFactory.consentId)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}