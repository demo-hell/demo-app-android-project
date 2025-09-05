package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.data.datasource.DetainerDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DetainerResumeRepositoryTest {
    private val remoteDataSource = mockk<DetainerDataSource>()
    private val repository = DetainerRemoteRepositoryImpl(remoteDataSource)
    private val successResponse = OpenFinanceFactory.successReponseDetainerResume
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return successful response if there is a payment summary from the detainer`() =
        runBlocking {

            coEvery { remoteDataSource.getDetainer(EMPTY) } returns resultSuccess

            val result = repository.getDetainer(EMPTY)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return an error response if there is no detainer payment summary`() = runBlocking {

        coEvery { remoteDataSource.getDetainer(EMPTY) } returns OpenFinanceFactory.resultError

        val result = repository.getDetainer(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if there is no holder payment summary`() = runBlocking {

        coEvery { remoteDataSource.getDetainer(EMPTY) } returns OpenFinanceFactory.resultEmpty

        val result = repository.getDetainer(EMPTY)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }

}