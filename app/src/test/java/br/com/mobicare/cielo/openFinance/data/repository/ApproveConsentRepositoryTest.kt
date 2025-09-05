package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.data.datasource.ApproveConsentDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ApproveConsentRepositoryTest {
    private val remoteDataSource = mockk<ApproveConsentDataSource>()
    private val repository = ApproveConsentRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successResponseConsent
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response if consent approval is correct`() =
        runBlocking {

            coEvery { remoteDataSource.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY) } returns resultSuccess

            val result = repository.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if consent approval is incorrect`() = runBlocking {

        coEvery { remoteDataSource.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY)} returns OpenFinanceFactory.resultError

        val result = repository.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if consent approval is empty`() = runBlocking {

        coEvery { remoteDataSource.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY)} returns OpenFinanceFactory.resultEmpty

        val result = repository.approveConsent(OpenFinanceFactory.consentIdRequest, EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}