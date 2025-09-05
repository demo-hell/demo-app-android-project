package br.com.mobicare.cielo.meuCadastroNovo.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.ContactPreference
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PcdType
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TimeOfDay
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.domain.repository.MyAccountRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GetAdditionalFieldsInfoUseCaseTest {

    private val repository = mockk<MyAccountRepository>()
    private val getAdditionalFieldsInfoUseCase = GetAdditionalFieldsInfoUseCase(repository)

    @Test
    fun `invoke should return AdditionalFields data on successful API response`() =
        runBlocking {
            val timeOfDay = arrayListOf(TimeOfDay("code", "description"))
            val typeOfCommunication = arrayListOf(TypeOfCommunication("code", "description"))
            val contactPreference = arrayListOf(ContactPreference("code", "description"))
            val pcdType = arrayListOf(PcdType("code", "description"))

            val response = GetAdditionalInfoFields(
                timeOfDay = timeOfDay,
                typeOfCommunication = typeOfCommunication,
                contactPreference = contactPreference,
                pcdType = pcdType
            )

            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                repository.getAdditionalFieldsInfo()
            } coAnswers {
                resultSuccess
            }

            val result = getAdditionalFieldsInfoUseCase()

            TestCase.assertEquals(resultSuccess, result)
        }

    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            repository.getAdditionalFieldsInfo()
        } coAnswers {
            resultError
        }

        val result = getAdditionalFieldsInfoUseCase()

        TestCase.assertEquals(resultError, result)
    }
}