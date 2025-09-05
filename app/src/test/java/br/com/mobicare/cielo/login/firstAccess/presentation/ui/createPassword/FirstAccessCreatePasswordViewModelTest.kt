package br.com.mobicare.cielo.login.firstAccess.presentation.ui.createPassword

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessType.REQUEST_ADM_PERMISSION
import br.com.mobicare.cielo.login.firstAccess.domain.usecase.FirstAccessRegistrationUseCase
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory.mockCpf
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory.mockEmail
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory.mockNumberEc
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory.mockPassword
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessFactory.mockPasswordConfirmation
import br.com.mobicare.cielo.login.firstAccess.utils.FirstAccessUiState
import com.akamai.botman.CYFMonitor
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FirstAccessCreatePasswordViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val firstAccessRegistrationUseCase = mockk<FirstAccessRegistrationUseCase>()

    private lateinit var viewModel: FirstAccessCreatePasswordViewModel
    private val responseFirstAccessSuccess = FirstAccessFactory.getFirstAccessResponseSuccess()
    private val resultFirstAccessEmpty = CieloDataResult.Empty()

    @Before
    fun setup(){
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)

        viewModel = FirstAccessCreatePasswordViewModel(firstAccessRegistrationUseCase)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test sendRequest success`() =
        runTest {
            prepareResources(result = CieloDataResult.Success(responseFirstAccessSuccess))

            viewModel.sendRequest(
                mockNumberEc, mockCpf, mockEmail, mockPassword, mockPasswordConfirmation
            )

            coVerify { firstAccessRegistrationUseCase(any(),any(), any()) }

            viewModel.firstAccessLiveData.value.let {
                assert(it is FirstAccessUiState.FirstAccessSuccess
                        && responseFirstAccessSuccess == it.firstAccessResult)
            }
        }

    @Test
    fun `test sendRequest error`() =
        runTest {
            prepareAkamai()
            val errorResult = CieloDataResult.APIError(
                CieloAPIException(
                    actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                    newErrorMessage = NewErrorMessage(
                        httpCode = 403,
                        flagErrorCode = REQUEST_ADM_PERMISSION.errorType,
                        message = FirstAccessFactory.errorMessage
                    )
                )
            )

            coEvery { firstAccessRegistrationUseCase(any(),any(), any()) } returns errorResult

            viewModel.sendRequest(
                mockNumberEc, mockCpf, mockEmail, mockPassword, mockPasswordConfirmation
            )

            viewModel.firstAccessLiveData.value?.let {
                assert(it is FirstAccessUiState.FirstAccessErrorMessage
                        && FirstAccessFactory.errorMessage == it.message)
            }
        }

    @Test
    fun `test sendRequest generic error`() =
        runTest {
            prepareResources(result = resultFirstAccessEmpty)

            viewModel.sendRequest(
                mockNumberEc, mockCpf, mockEmail, mockPassword, mockPasswordConfirmation
            )

            coVerify { firstAccessRegistrationUseCase(any(),any(), any()) }

            viewModel.firstAccessLiveData.value.let {
                assert(it is FirstAccessUiState.FirstAccessErrorGeneric)
            }
        }

    @Test
    fun `test sendRequest akamai error`() = runTest {
            val response = FirstAccessFactory.getNotBootingError()
            prepareResources(result = CieloDataResult.APIError(response))

            viewModel.sendRequest(
                mockNumberEc, mockCpf, mockEmail, mockPassword, mockPasswordConfirmation
            )

            coVerify { firstAccessRegistrationUseCase(any(),any(), any()) }

            viewModel.firstAccessLiveData.value.let {
                assert(it is FirstAccessUiState.FirstAccessErrorNotBooting)
            }
        }

    private fun prepareResources(result: CieloDataResult<FirstAccessResponse>) {
        coEvery {
            firstAccessRegistrationUseCase(any(),any(), any())
        } coAnswers {
            result
        }
        prepareAkamai()
    }

    private fun prepareAkamai() {

        mockkStatic(CYFMonitor::class)
        coEvery {
            CYFMonitor.getSensorData()
        } returns "akamaiSensor"
    }
}