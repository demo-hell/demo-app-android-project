package br.com.mobicare.cielo.antiFraud.data

import br.com.mobicare.cielo.antiFraud.utils.AntiFraudFactory
import br.com.mobicare.cielo.antifraud.data.dataSource.AntiFraudDataSource
import br.com.mobicare.cielo.antifraud.data.repository.AntiFraudRepositoryImpl
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AntiFraudRepositoryTest {

    private val remoteDataSource = mockk<AntiFraudDataSource>()
    private val repository = AntiFraudRepositoryImpl(remoteDataSource)

    private val resultSuccessGetSessionID = CieloDataResult.Success(AntiFraudFactory.sessionID)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Test
    fun `it should fetch sessionID calling remote data source only once`() = runBlocking {
        coEvery {
            remoteDataSource.getSessionID()
        } returns resultSuccessGetSessionID

        repository.getSessionID()

        coVerify(exactly = ONE) { remoteDataSource.getSessionID() }
    }

    @Test
    fun `it should return the sessionID`() = runBlocking {
        coEvery {
            remoteDataSource.getSessionID()
        } returns resultSuccessGetSessionID

        val result = repository.getSessionID()

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value

        assertEquals(resultSuccessGetSessionID.value, actualData)
    }

    @Test
    fun `it should return a API error when get sessionID`() = runBlocking {
        coEvery {
            remoteDataSource.getSessionID()
        } returns resultError

        val result = repository.getSessionID()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when get sessionID`() = runBlocking {
        coEvery {
            remoteDataSource.getSessionID()
        } returns resultEmpty

        val result = repository.getSessionID()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}