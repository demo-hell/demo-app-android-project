package br.com.mobicare.cielo.forgotMyPassword.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.forgotMyPassword.data.dataSource.ForgotMyPasswordRemoteDataSource
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotMyPasswordInsertInfoFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ForgotMyPasswordRepositoryTest {

    private val dataSource = mockk<ForgotMyPasswordRemoteDataSource>()
    private val params = mockk<ForgotMyPasswordRecoveryPasswordRequest>()
    private val akamaiSensorData = EMPTY
    private val repository = ForgotMyPasswordRepositoryImpl(dataSource)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `postForgotMyPasswordRecoveryPassword should return success result on successful API response`() =
        runBlocking {
            val response = ForgotMyPasswordInsertInfoFactory.makeForgotMyPassword()
            val resultSuccess = CieloDataResult.Success(response)

            prepareScenario(result = resultSuccess)

            val result = repository.postForgotMyPasswordRecoveryPassword(params, akamaiSensorData)

            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `postForgotMyPasswordRecoveryPassword should return error result on error API response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            prepareScenario(result = resultError)

            val result = repository.postForgotMyPasswordRecoveryPassword(params, akamaiSensorData)

            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `postForgotMyPasswordRecoveryPassword should return akamai error result on akamai error API response`() =
        runBlocking {
            val response = ForgotMyPasswordInsertInfoFactory.makeNotBootingError()
            val resulAkamaiError = CieloDataResult.APIError(response)

            prepareScenario(result = resulAkamaiError)

            val result = repository.postForgotMyPasswordRecoveryPassword(params, akamaiSensorData)

            TestCase.assertEquals(resulAkamaiError, result)
        }

    private fun prepareScenario(result: CieloDataResult<ForgotMyPassword>) {
        coEvery {
            dataSource.postRecoveryPassword(any(), any())
        } coAnswers {
            result
        }
    }

}