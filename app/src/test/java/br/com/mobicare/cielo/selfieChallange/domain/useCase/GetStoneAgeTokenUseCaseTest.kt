package br.com.mobicare.cielo.selfieChallange.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.selfieChallange.domain.repository.SelfieChallengeRepository
import br.com.mobicare.cielo.selfieChallange.domain.usecase.GetStoneAgeTokenUseCase
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetStoneAgeTokenUseCaseTest {

    private val repository = mockk<SelfieChallengeRepository>()
    private val getStoneAgeTokenUseCase = GetStoneAgeTokenUseCase(repository)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return TOKEN data on successful API response`() =
        runBlocking {
            val response = "token"
            val resultSuccess = CieloDataResult.Success(response)

            prepareScenario(result = resultSuccess)

            val result = getStoneAgeTokenUseCase()

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        prepareScenario(result = resultError)

        val result = getStoneAgeTokenUseCase()

        TestCase.assertEquals(resultError, result)
    }

    private fun prepareScenario(result: CieloDataResult<String>) {
        coEvery {
            repository.getStoneAgeToken()
        } coAnswers {
            result
        }
    }

}