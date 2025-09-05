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

class PostAssignRoleUseCaseTest {

    private val repository = mockk<NewAccessManagerRepository>()
    private val postAssignRoleUseCase = PostAssignRoleUseCase(repository)
    private val usersId = listOf<String>()
    private val role = "ADMIN"
    private val otpCode = "123456"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return successful API response`() =
        runBlocking {
            val resultEmpty = CieloDataResult.Empty()

            coEvery {
                repository.postAssignRole(any(), any(), any())
            } coAnswers {
                resultEmpty
            }

            val result = repository.postAssignRole(usersId, role, otpCode)

            TestCase.assertEquals(resultEmpty, result)
        }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            repository.postAssignRole(any(), any(), any())
        } coAnswers {
            resultError
        }

        val result = repository.postAssignRole(usersId, role, otpCode)

        TestCase.assertEquals(resultError, result)
    }

}