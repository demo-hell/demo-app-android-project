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

class PutAdditionalInfoUseCaseTest {

    private val repository = mockk<MyAccountRepository>()
    private val putAdditionalInfoUseCase = PutAdditionalInfoUseCase(repository)
    val timeOfDay = "timeOfDay"
    val typeOfCommunication = arrayListOf("typeOfCommunication")
    val contactPreference = "contactPreference"
    val pcdType = "pcdType"

    @Test
    fun `invoke should return Message data on successful API response`() =
        runBlocking {
            val response = "Success"

            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                repository.putAdditionalInfo(any(), any(), any(), any())
            } coAnswers {
                resultSuccess
            }

            val result =
                putAdditionalInfoUseCase(timeOfDay, typeOfCommunication, contactPreference, pcdType)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            repository.putAdditionalInfo(any(), any(), any(), any())
        } coAnswers {
            resultError
        }

        val result =
            putAdditionalInfoUseCase(timeOfDay, typeOfCommunication, contactPreference, pcdType)

        TestCase.assertEquals(resultError, result)
    }
}