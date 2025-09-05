package br.com.mobicare.cielo.openFinance.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.data.datasource.EndShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class EndShareRepositoryTest {
    private val remoteDataSource = mockk<EndShareDataSource>()
    private val repository = EndShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.endShareRequest
    private val resultSuccess = CieloDataResult.Success(Any())

    @Test
    fun `it should return a successful response if end sharing is correct`() =
        runBlocking {

            coEvery { remoteDataSource.endShare(EMPTY, request) } returns resultSuccess

            val result = repository.endShare(EMPTY, request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response if end sharing is incorrect`() = runBlocking {

        coEvery { remoteDataSource.endShare(EMPTY, request) } returns OpenFinanceFactory.resultError

        val result = repository.endShare(EMPTY, request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response if end sharing is empty`() = runBlocking {

        coEvery { remoteDataSource.endShare(EMPTY, request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.endShare(EMPTY, request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}