package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PostUserValidateDataUseCaseTest {
    private val repository = mockk<MyAccountRepository>()
    private val postUserValidateDataUseCase = PostUserValidateDataUseCase(repository)
    val email = "email"
    val password = "password"
    val passwordConfirmation = "passwordConfirmation"
    val cellphone = "cellphone"

    @Test
    fun `invoke should return FACEIDTOKEN data on successful API response`() =
        runBlocking {
            val response = "sucesso"
            val resultSuccess = CieloDataResult.Success(response)

            prepareScenario(result = resultSuccess)

            val result = postUserValidateDataUseCase(email, password, passwordConfirmation, cellphone)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        prepareScenario(result = resultError)

        val result = postUserValidateDataUseCase(email, password, passwordConfirmation, cellphone)

        TestCase.assertEquals(resultError, result)
    }

    private fun prepareScenario(result: CieloDataResult<String>) {
        coEvery {
            repository.postUserDataValidation(any(), any(), any(), any())
        } coAnswers {
            result
        }
    }
}