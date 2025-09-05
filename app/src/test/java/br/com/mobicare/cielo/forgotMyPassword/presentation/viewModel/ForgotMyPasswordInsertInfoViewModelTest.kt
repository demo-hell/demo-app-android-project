package br.com.mobicare.cielo.forgotMyPassword.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.forgotMyPassword.domain.model.ForgotMyPassword
import br.com.mobicare.cielo.forgotMyPassword.domain.useCase.PostForgotMyPasswordRecoveryPasswordUseCase
import br.com.mobicare.cielo.forgotMyPassword.presentation.insertInfo.ForgotMyPasswordInsertInfoViewModel
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotMyPasswordInsertInfoFactory
import br.com.mobicare.cielo.forgotMyPassword.utils.ForgotPasswordUiState
import com.akamai.botman.CYFMonitor
import io.mockk.*
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
class ForgotMyPasswordInsertInfoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val forgotMyPasswordRecoveryPasswordUseCase =
        mockk<PostForgotMyPasswordRecoveryPasswordUseCase>()
    private val userPreferences = mockk<UserPreferences>()

    private lateinit var viewModel: ForgotMyPasswordInsertInfoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)

        viewModel = ForgotMyPasswordInsertInfoViewModel(
            forgotMyPasswordRecoveryPasswordUseCase,
            userPreferences
        )
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `test sendRequestRecoveryPassword success`() = runTest {
        val userName = "testUser"
        val response = ForgotMyPasswordInsertInfoFactory.makeForgotMyPassword()

        prepareScenario(result = CieloDataResult.Success(response))

        viewModel.sendRequestRecoveryPassword(userName)

        coVerify { forgotMyPasswordRecoveryPasswordUseCase(any(), any()) }

        viewModel.forgotPasswordUiState.value.let {
            assert(it is ForgotPasswordUiState.Success && response == it.data)
        }
    }

    @Test
    fun `test sendRequestRecoveryPassword error`() = runTest {
        val userName = "testUser"

        prepareScenario(result = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY)))

        viewModel.sendRequestRecoveryPassword(userName)

        coVerify { forgotMyPasswordRecoveryPasswordUseCase(any(), any()) }

        viewModel.forgotPasswordUiState.value.let {
            assert(it is ForgotPasswordUiState.Error)
        }
    }

    @Test
    fun `test sendRequestRecoveryPassword akamai error`() = runTest {
        val userName = "testUser"
        val response = ForgotMyPasswordInsertInfoFactory.makeNotBootingError()

        prepareScenario(result = CieloDataResult.APIError(response))

        viewModel.sendRequestRecoveryPassword(userName)

        coVerify { forgotMyPasswordRecoveryPasswordUseCase(any(), any()) }

        viewModel.forgotPasswordUiState.value.let {
            assert(it is ForgotPasswordUiState.ErrorAkamai)
        }
    }

    @Test
    fun `deleteUserInformation should call userPreferences delete method`() {
        every {
            userPreferences.deleteUserInformation()
        } just runs

        viewModel.deleteUserInformation()

        verify(exactly = 1) { userPreferences.deleteUserInformation() }
    }

    private fun prepareScenario(result: CieloDataResult<ForgotMyPassword>) {
        coEvery {
            forgotMyPasswordRecoveryPasswordUseCase(any(), any())
        } coAnswers {
            result
        }

        mockkStatic(CYFMonitor::class)
        coEvery {
            CYFMonitor.getSensorData()
        } returns "akamaiSensor"
    }
}