package br.com.mobicare.cielo.openFinance.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.openFinance.data.datasource.UpdateShareDataSource
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UpdateShareRepositoryTest {
    private val remoteDataSource = mockk<UpdateShareDataSource>()
    private val repository = UpdateShareRepositoryImpl(remoteDataSource)
    private val request = OpenFinanceFactory.requestUpdateShare
    private val successResponse = OpenFinanceFactory.responseUpdateShare
    private val resultSuccess = CieloDataResult.Success(successResponse)

    @Test
    fun `it should return a successful response with data for update share`() =
        runBlocking {

            coEvery { remoteDataSource.updateShare(EMPTY, request) } returns resultSuccess

            val result = repository.updateShare(EMPTY, request)

            Assert.assertEquals(resultSuccess, result)
        }

    @Test
    fun `it should return a error response without data for update share`() = runBlocking {

        coEvery { remoteDataSource.updateShare(EMPTY, request) } returns OpenFinanceFactory.resultError

        val result = repository.updateShare(EMPTY, request)

        Assert.assertEquals(OpenFinanceFactory.resultError, result)
    }

    @Test
    fun `it should return an empty response without data for update share`() = runBlocking {

        coEvery { remoteDataSource.updateShare(EMPTY, request) } returns OpenFinanceFactory.resultEmpty

        val result = repository.updateShare(EMPTY, request)

        Assert.assertEquals(OpenFinanceFactory.resultEmpty, result)
    }
}