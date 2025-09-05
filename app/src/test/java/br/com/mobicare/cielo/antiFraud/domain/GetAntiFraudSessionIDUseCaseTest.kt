package br.com.mobicare.cielo.antiFraud.domain

import br.com.mobicare.cielo.antiFraud.utils.AntiFraudFactory
import br.com.mobicare.cielo.antifraud.ThreatMetrixProfiler
import br.com.mobicare.cielo.antifraud.domain.repository.AntiFraudRepository
import br.com.mobicare.cielo.antifraud.domain.useCase.GetAntiFraudSessionIDUseCase
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAntiFraudSessionIDUseCaseTest {

    private val repository = mockk<AntiFraudRepository>()

    private lateinit var getAntiFraudSessionIDUseCase: GetAntiFraudSessionIDUseCase
    private lateinit var profiler: ThreatMetrixProfiler

    private val resultSuccessGetSessionID = CieloDataResult.Success(AntiFraudFactory.sessionID)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    @Before
    fun setup(){
        profiler = mockk(relaxed = true)
        getAntiFraudSessionIDUseCase = GetAntiFraudSessionIDUseCase(repository, profiler)
    }

   /* @Test
    fun `it should fetch sessionID calling remote data source only once`() = runBlocking {
        coEvery {
            repository.getSessionID()
        } returns resultSuccessGetSessionID
        coEvery {
            profiler.analyzeUserDeviceToSuspend(any())
        } returns resultSuccessGetSessionID

        getAntiFraudSessionIDUseCase()

        coVerify(exactly = ONE) { repository.getSessionID() }
    }*/

   /* @Test
    fun `it should return the sessionID`() = runBlocking {
        coEvery {
            repository.getSessionID()
        } returns resultSuccessGetSessionID
        coEvery {
            profiler.analyzeUserDeviceToSuspend(any())
        } returns resultSuccessGetSessionID

        val result = getAntiFraudSessionIDUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Success::class.java)

        val resultSuccess = result as CieloDataResult.Success
        val actualData = resultSuccess.value

        assertEquals(resultSuccessGetSessionID.value, actualData)
    }*/

   /* @Test
    fun `it should return empty error when analyze user was false`() = runBlocking {
        coEvery {
            repository.getSessionID()
        } returns resultSuccessGetSessionID
        coEvery {
            profiler.analyzeUserDeviceToSuspend(any())
        } returns resultEmpty

        val result = getAntiFraudSessionIDUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }*/

    @Test
    fun `it should return a API error when get sessionID`() = runBlocking {
        coEvery {
            repository.getSessionID()
        } returns resultError

        val result = getAntiFraudSessionIDUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.APIError::class.java)
        assertEquals(resultError, result)
    }

    @Test
    fun `it should return a empty error when get sessionID`() = runBlocking {
        coEvery {
            repository.getSessionID()
        } returns resultEmpty

        val result = getAntiFraudSessionIDUseCase()

        assertThat(result).isInstanceOf(CieloDataResult.Empty::class.java)
        assertEquals(resultEmpty, result)
    }

}