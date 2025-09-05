package br.com.mobicare.cielo.selfieChallange.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.selfieChallange.data.datasource.remote.SelfieChallengeRemoteDataSource
import br.com.mobicare.cielo.selfieChallange.data.repository.SelfieChallengeRepositoryImpl
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SelfieChallengeRepositoryTest {

    private val dataSource = mockk<SelfieChallengeRemoteDataSource>()
    private val repository = SelfieChallengeRepositoryImpl(dataSource)
    val base64 = "base64"
    val encrypted = "encrypted"
    val username = "username"
    val operation = "operation"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getStoneAgeToken should return success result on successful API response`() =
        runBlocking {
            val response = "token"
            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                dataSource.getStoneAgeToken()
            }coAnswers {
                resultSuccess
            }

            val result = repository.getStoneAgeToken()

            TestCase.assertEquals(resultSuccess, result)
        }
    @Test
    fun `getStoneAgeToken should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.getStoneAgeToken()
            }coAnswers {
                resultError
            }

            val result = repository.getStoneAgeToken()

            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `postSelfieChallenge should return success result on successful API response`() =
        runBlocking {
            val response = "token"
            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                dataSource.postSelfieChallenge(any(), any(), any(), any())
            }coAnswers {
                resultSuccess
            }

            val result = repository.postSelfieChallenge(base64, encrypted, username, operation)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `postSelfieChallenge should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.postSelfieChallenge(any(), any(), any(), any())
            }coAnswers {
                resultError
            }

            val result = repository.postSelfieChallenge(base64, encrypted, username, operation)

            TestCase.assertEquals(resultError, result)
        }

}