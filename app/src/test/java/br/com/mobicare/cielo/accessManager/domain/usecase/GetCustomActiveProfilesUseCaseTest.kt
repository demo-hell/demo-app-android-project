package br.com.mobicare.cielo.accessManager.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.accessManager.domain.repository.NewAccessManagerRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetCustomActiveProfilesUseCaseTest {

    private val repository = mockk<NewAccessManagerRepository>()
    private val getCustomActiveProfilesUseCase = GetCustomActiveProfilesUseCase(repository)
    private val profileType = "profileType"
    private val status = "status"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return list data on successful API response`() =
        runBlocking {
            val response = listOf<CustomProfiles>()
            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                repository.getCustomActiveProfiles(any(), any())
            } coAnswers {
                resultSuccess
            }

            val result = getCustomActiveProfilesUseCase(profileType, status)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            repository.getCustomActiveProfiles(any(), any())
        } coAnswers {
            resultError
        }

        val result =  getCustomActiveProfilesUseCase(profileType, status)

        TestCase.assertEquals(resultError, result)
    }
}