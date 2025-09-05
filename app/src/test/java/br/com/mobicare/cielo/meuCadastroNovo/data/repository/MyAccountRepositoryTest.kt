package br.com.mobicare.cielo.meuCadastroNovo.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.meuCadastroNovo.data.datasource.remote.MyAccountRemoteDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class MyAccountRepositoryTest {

    private val dataSource = mockk<MyAccountRemoteDataSource>()
    private val repository = MyAccountRepositoryImpl(dataSource)
    val email = "email"
    val password = "password"
    val passwordConfirmation = "passwordConfirmation"
    val cellphone = "cellphone"
    val faceIdToken = "faceIdToken"

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `postUserDataValidation should return success result on successful API response`() =
        runBlocking {
            val resultSuccess = CieloDataResult.Success("sucesso")

            coEvery {
                dataSource.postUserDataValidation(any(), any(), any(), any())
            }coAnswers {
                resultSuccess
            }

            val result = repository.postUserDataValidation(email, password, passwordConfirmation, cellphone)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `postUserDataValidation should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.postUserDataValidation(any(), any(), any(), any())
            }coAnswers {
                resultError
            }

            val result = repository.postUserDataValidation(email, password, passwordConfirmation, cellphone)

            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `putUserUpdateData should return success result on successful API response`() =
        runBlocking {
            val resultSuccess = CieloDataResult.Success("sucesso")

            coEvery {
                dataSource.putUserUpdateData(any(), any(), any(), any(), any())
            }coAnswers {
                resultSuccess
            }

            val result = repository.putUserUpdateData(email, password, passwordConfirmation, cellphone, faceIdToken)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `putUserUpdateData should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                dataSource.putUserUpdateData(any(), any(), any(), any(), any())
            }coAnswers {
                resultError
            }

            val result = repository.putUserUpdateData(email, password, passwordConfirmation, cellphone, faceIdToken)

            TestCase.assertEquals(resultError, result)
        }
}