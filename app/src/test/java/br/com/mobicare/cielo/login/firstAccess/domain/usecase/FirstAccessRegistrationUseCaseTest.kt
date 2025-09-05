package br.com.mobicare.cielo.login.firstAccess.domain.usecase

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.REQUEST_ADM_PERMISSION
import br.com.mobicare.cielo.login.firstAccess.domain.repository.FirstAccessRepository
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory
import com.akamai.botman.CYFMonitor
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test

class FirstAccessRegistrationUseCaseTest {
    private val repository = mockk<FirstAccessRepository>()
    private val useCase = FirstAccessRegistrationUseCase(repository)
    private val akamai = prepareAkamai().toString()
    val token = EMPTY
    val request = FirstAccessFactory.firstAccessRegistrationRequest

    private val responseFirstAccessSuccess = FirstAccessFactory.getFirstAccessResponseSuccess()

    @Test
    fun `invoke should registrationAccount return success result on successful response`() =
        runBlocking {
            val resultSuccess = CieloDataResult.Success(responseFirstAccessSuccess)
            prepareResources(result = resultSuccess)

            val result = useCase(request, token, akamai)
            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should registrationAccount return INELIGIBLE_MERCHANT_ID result on error response`() =
        runBlocking {
            val resultError = CieloDataResult.APIError(
                CieloAPIException(
                    actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                    newErrorMessage = NewErrorMessage(
                        httpCode = 403,
                        flagErrorCode = REQUEST_ADM_PERMISSION.errorType,
                        message = FirstAccessFactory.errorMessage
                    )
                )
            )
            prepareResources(result = resultError)

            val result = useCase(request, token, akamai)
            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `invoke should registrationAccount return error result on error response`() =
        runBlocking {
            val resultError = CieloDataResult.Empty()
            prepareResources(result = resultError)

            val result = useCase(request, token, akamai)
            TestCase.assertEquals(resultError, result)
        }

    private fun prepareResources(result: CieloDataResult<FirstAccessResponse>) {
        coEvery {
            repository.registrationAccount(any(),any(), any())
        } coAnswers {
            result
        }
    }

    private fun prepareAkamai() {
        mockkStatic(CYFMonitor::class)
        coEvery {
            CYFMonitor.getSensorData()
        } returns "akamaiSensor"
    }
}