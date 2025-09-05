package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userDataChange

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.meuCadastroNovo.constants.UserDataConstants.INVALID_DATA
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PostUserValidateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutUserUpdateDataUseCase
import br.com.mobicare.cielo.meuCadastroNovo.utils.UserDataChangeUiState
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserDataChangeViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val postUserValidateDataUseCase = mockk<PostUserValidateDataUseCase>()
    private val putUserUpdateDataUseCase = mockk<PutUserUpdateDataUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private lateinit var viewModel: UserDataChangeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)

        viewModel = UserDataChangeViewModel(
            postUserValidateDataUseCase,
            putUserUpdateDataUseCase,
            getUserObjUseCase
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `postUserValidateData should emit UserValidateSuccess when validation is successful`() = runTest {
        // Arrange
        val successResult = CieloDataResult.Success("Success")
        val response = "Success"

        coEvery {
            postUserValidateDataUseCase.invoke(any(), any(), any(), any())
        } returns successResult

        // Act
        viewModel.postUserValidateData("email", "password", "confirmation", "cellphone")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.UserValidateSuccess && response == it.response)
        }
    }

    @Test
    fun `postUserValidateData should emit GenericError when validation fails`() = runTest {
        // Arrange
        val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            postUserValidateDataUseCase.invoke(any(), any(), any(), any())
        } returns errorResult

        // Act
        viewModel.postUserValidateData("email", "password", "confirmation", "cellphone")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.GenericError)
        }
    }

    @Test
    fun `postUserValidateData should emit UserValidateEmailError when validation email fails`() = runTest {
        // Arrange
        val errorMessage = "new_email: erro no email"
        val errorResult = CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                newErrorMessage = NewErrorMessage(
                    httpCode = 420,
                    flagErrorCode = INVALID_DATA,
                    message = errorMessage
                )
            )
        )
        coEvery {
            postUserValidateDataUseCase.invoke(any(), any(), any(), any())
        } returns errorResult

        // Act
        viewModel.postUserValidateData("email", "password", "confirmation", "cellphone")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.UserValidateEmailError && errorMessage == it.message)
        }
    }

    @Test
    fun `postUserValidateData should emit UserValidatePasswordError when validation password fails`() = runTest {
        // Arrange
        val errorMessage = "password: erro na senha"
        val errorResult = CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                newErrorMessage = NewErrorMessage(
                    httpCode = 420,
                    flagErrorCode = INVALID_DATA,
                    message = errorMessage
                )
            )
        )
        coEvery {
            postUserValidateDataUseCase.invoke(any(), any(), any(), any())
        } returns errorResult

        // Act
        viewModel.postUserValidateData("email", "password", "confirmation", "cellphone")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.UserValidatePasswordError && errorMessage == it.message)
        }
    }

    @Test
    fun `postUserValidateData should emit UserValidatePhoneError when validation phone fails`() = runTest {
        // Arrange
        val errorMessage = "new_phone: erro no telefone"
        val errorResult = CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                newErrorMessage = NewErrorMessage(
                    httpCode = 420,
                    flagErrorCode = INVALID_DATA,
                    message = errorMessage
                )
            )
        )
        coEvery {
            postUserValidateDataUseCase.invoke(any(), any(), any(), any())
        } returns errorResult

        // Act
        viewModel.postUserValidateData("email", "password", "confirmation", "cellphone")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.UserValidatePhoneError && errorMessage == it.message)
        }
    }

    @Test
    fun `putUserUpdateData should emit UserUpdateSuccess when update is successful`() = runTest {
        // Arrange
        val response = "Update Success"
        val successResult = CieloDataResult.Success(response)

        coEvery {
            putUserUpdateDataUseCase.invoke(any(), any(), any(), any(), any())
        } returns successResult

        // Act
        viewModel.putUserUpdateData("email", "password", "confirmation", "cellphone", "faceIdToken")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.UserUpdateSuccess && response == it.response)
        }
    }

    @Test
    fun `putUserUpdateData should emit GenericError when validation fails`() = runTest {
        // Arrange
        val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

        coEvery {
            putUserUpdateDataUseCase.invoke(any(), any(), any(), any(), any())
        } returns errorResult

        // Act
        viewModel.putUserUpdateData("email", "password", "confirmation", "cellphone", "faceIdToken")

        // Assert
        viewModel.userDataChangeLiveData.value?.let {
            assert(it is UserDataChangeUiState.GenericError)
        }
    }

}