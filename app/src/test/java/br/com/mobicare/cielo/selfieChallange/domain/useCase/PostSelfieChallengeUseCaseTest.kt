package br.com.mobicare.cielo.selfieChallange.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository
import br.com.mobicare.cielo.selfieChallange.domain.usecase.PostSelfieChallengeUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PostSelfieChallengeUseCaseTest {

    private val repository = mockk<SelfieChallengeRepository>()
    private val postSelfieChallengeUseCase = PostSelfieChallengeUseCase(repository)
    val base64 = "base64"
    val encrypted = "encrypted"
    val username = "username"
    val operation = "operation"
    @Test
    fun `invoke should return FACEIDTOKEN data on successful API response`() =
        runBlocking {
            val response = "faceIdToken"
            val resultSuccess = CieloDataResult.Success(response)

            prepareScenario(result = resultSuccess)

            val result = postSelfieChallengeUseCase(base64, encrypted, username, operation)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        prepareScenario(result = resultError)

        val result = postSelfieChallengeUseCase(base64, encrypted, username, operation)

        TestCase.assertEquals(resultError, result)
    }

    private fun prepareScenario(result: CieloDataResult<String>) {
        coEvery {
            repository.postSelfieChallenge(any(), any(), any(), any())
        } coAnswers {
            result
        }
    }

}