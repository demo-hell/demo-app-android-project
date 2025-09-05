package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.RejectConsentDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RejectConsentRepositoryTest {
    private val remoteDataSource = mockk<RejectConsentDataSource>()
    private val repository = RejectConsentRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successResponseConsent
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response if consent refusing is correct`() =
        runBlocking {

            coEvery {
                remoteDataSource.rejectConsent(
                    OpenFinanceFactory.rejectConsentRequest
                )
            } returns resultSuccess

            val result = repository.rejectConsent(
                OpenFinanceFactory.rejectConsentRequest
            )

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if consent refusing is incorrect`() = runBlocking {

        coEvery {
            remoteDataSource.rejectConsent(
                OpenFinanceFactory.rejectConsentRequest
            )
        } returns OpenFinanceFactory.resultError

        val result = repository.rejectConsent(
            OpenFinanceFactory.rejectConsentRequest
        )

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if consent refusing is empty`() = runBlocking {

        coEvery {
            remoteDataSource.rejectConsent(
                OpenFinanceFactory.rejectConsentRequest
            )
        } returns OpenFinanceFactory.resultEmpty

        val result = repository.rejectConsent(
            OpenFinanceFactory.rejectConsentRequest
        )

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}