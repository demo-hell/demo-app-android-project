package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.constants.ONE_TEXT
import br.com.mobicare.cielo.commons.constants.TWENTYFIVE_TEXT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.SharedDataConsentsDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants.RECEIVING_JOURNEY
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class SharedDataRepositoryTest {
    private val remoteDataSource = mockk<SharedDataConsentsDataSource>()
    private val repository = SharedDataConsentsRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successResponseSharedDataConsent
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response if shared data consent is correct`() =
        runBlocking {

            coEvery {
                remoteDataSource.getConsents(
                    RECEIVING_JOURNEY,
                    ONE_TEXT,
                    TWENTYFIVE_TEXT
                )
            } returns resultSuccess

            val result = repository.getConsents(RECEIVING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if shared data consent is incorrect`() = runBlocking {

        coEvery {
            remoteDataSource.getConsents(
                RECEIVING_JOURNEY,
                ONE_TEXT,
                TWENTYFIVE_TEXT
            )
        } returns OpenFinanceFactory.resultError

        val result = repository.getConsents(RECEIVING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if shared data consent is empty`() = runBlocking {

        coEvery {
            remoteDataSource.getConsents(
                RECEIVING_JOURNEY,
                ONE_TEXT,
                TWENTYFIVE_TEXT
            )
        } returns OpenFinanceFactory.resultEmpty

        val result = repository.getConsents(RECEIVING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }

}