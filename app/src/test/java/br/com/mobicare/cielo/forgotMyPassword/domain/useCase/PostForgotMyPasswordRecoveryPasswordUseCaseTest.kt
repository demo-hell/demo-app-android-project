package br.com.mobicare.cielo.forgotMyPassword.domain.useCase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.forgotMyPassword.data.model.request.ForgotMyPasswordRecoveryPasswordRequest
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.domain.repository.ForgotMyPasswordRepository
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotMyPasswordInsertInfoFactory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PostForgotMyPasswordRecoveryPasswordUseCaseTest {

    private val repository = mockk<ForgotMyPasswordRepository>()
    private val params = mockk<ForgotMyPasswordRecoveryPasswordRequest>()
    private val akamaiSensorData = EMPTY
    private val postForgotMyPasswordRecoveryPasswordUseCase =
        PostForgotMyPasswordRecoveryPasswordUseCase(repository)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should return ForgotMyPassword data on successful API response`() = runBlocking {
        val response = ForgotMyPasswordInsertInfoFactory.makeForgotMyPassword()
        val resultSuccess = CieloDataResult.Success(response)

        prepareScenario(result = resultSuccess)

        val result = postForgotMyPasswordRecoveryPasswordUseCase(params, akamaiSensorData)

        TestCase.assertEquals(CieloDataResult.Success(response), result)
    }

    @Test
    fun `invoke should return API error result on error response`() = runBlocking {
        val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        prepareScenario(result = resultError)

        val result = postForgotMyPasswordRecoveryPasswordUseCase(params, akamaiSensorData)

        TestCase.assertEquals(resultError, result)
    }

    @Test
    fun `invoke should return akamai error result on akamai error API response`() = runBlocking {
        val response = ForgotMyPasswordInsertInfoFactory.makeNotBootingError()
        val resulAkamaiError = CieloDataResult.APIError(response)

        prepareScenario(result = resulAkamaiError)

        val result = postForgotMyPasswordRecoveryPasswordUseCase(params, akamaiSensorData)

        TestCase.assertEquals(resulAkamaiError, result)
    }

    private fun prepareScenario(result: CieloDataResult<ForgotMyPassword>) {
        coEvery {
            repository.postForgotMyPasswordRecoveryPassword(any(), any())
        } coAnswers {
            result
        }
    }
}
