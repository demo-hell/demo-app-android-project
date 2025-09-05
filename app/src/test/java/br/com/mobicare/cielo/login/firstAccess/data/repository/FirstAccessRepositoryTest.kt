package br.com.mobicare.cielo.login.firstAccess.data.repository

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.firstAccess.data.datasource.FirstAccessDataSourceImpl
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.*
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory
import com.akamai.botman.CYFMonitor
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class FirstAccessRepositoryTest {

    private val remoteDataSource = mockk<FirstAccessDataSourceImpl>()
    private val repository = FirstAccessRepositoryImpl(remoteDataSource)


    private val akamai = prepareAkamai().toString()
    val token = EMPTY
    val request = FirstAccessFactory.firstAccessRegistrationRequest

    private val responseFirstAccessSuccess = FirstAccessFactory.getFirstAccessResponseSuccess()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `invoke should registrationAccount return success result on successful API response`() =
        runBlocking {
            val resultSuccess = CieloDataResult.Success(responseFirstAccessSuccess)

            coEvery {
                remoteDataSource.registrationAccount(request, token, akamai)
            }coAnswers {
                resultSuccess
            }

            val result = repository.registrationAccount(request, token, akamai)
            TestCase.assertEquals(resultSuccess, result)
        }

    @Test
    fun `invoke should registrationAccount return INELIGIBLE_MERCHANT_ID result on error API response`() =
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

            coEvery {
                remoteDataSource.registrationAccount(request, token, akamai)
            }coAnswers {
                resultError
            }

            val result = repository.registrationAccount(request, token, akamai)
            TestCase.assertEquals(resultError, result)
        }

    @Test
    fun `invoke should registrationAccount return error result on error response`() =
        runBlocking {
            val resultError = CieloDataResult.Empty()

            coEvery {
                remoteDataSource.registrationAccount(request, token, akamai)
            }coAnswers {
                resultError
            }

            val result = repository.registrationAccount(request, token, akamai)
            TestCase.assertEquals(resultError, result)
        }

    private fun prepareAkamai() {
        mockkStatic(CYFMonitor::class)
        coEvery {
            CYFMonitor.getSensorData()
        } returns "akamaiSensor"
    }
}