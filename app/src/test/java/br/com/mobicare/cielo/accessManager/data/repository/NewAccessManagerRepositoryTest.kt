package br.com.mobicare.cielo.accessManager.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.data.datasource.remote.AccessManagerRemoteDataSource
import br.com.mobicare.cielo.accessManager.domain.model.CustomProfiles
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class NewAccessManagerRepositoryTest {

    private val dataSource = mockk<AccessManagerRemoteDataSource>()
    private val repository = NewAccessManagerRepositoryImpl(dataSource)
    private val profileType = "profileType"
    private val status = "status"
    private val usersId = listOf<String>()
    private val role = "ADMIN"
    private val otpCode = "123456"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getCustomActiveProfiles should return success result on successful API response`() =
        runBlocking {
            val response = listOf<CustomProfiles>()
            val resultSuccess = CieloDataResult.Success(response)

            coEvery {
                dataSource.getCustomActiveProfiles(any(), any())
            } coAnswers {
                resultSuccess
            }

            val result = repository.getCustomActiveProfiles(profileType, status)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `getStoneAgeToken should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.getCustomActiveProfiles(any(), any())
            }coAnswers {
                resultError
            }

            val result = repository.getCustomActiveProfiles(profileType, status)

            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `postAssignRole should return success result on successful API response`() =
        runBlocking {
            val resultEmpty = CieloDataResult.Empty()

            coEvery {
                dataSource.postAssignRole(any(), any(), any())
            } coAnswers {
                resultEmpty
            }

            val result = repository.postAssignRole(usersId, role, otpCode)

            TestCase.assertEquals(resultEmpty, result)
        }

    @Test
    fun `postAssignRole should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.postAssignRole(any(), any(), any())
            } coAnswers {
                resultError
            }

            val result = repository.postAssignRole(usersId, role, otpCode)

            TestCase.assertEquals(resultError, result)
        }

}