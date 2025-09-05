package br.com.mobicare.cielo.meuCadastroNovo.presetantion.userAdditionalInfoChange

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.ContactPreference
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetAdditionalInfoFields
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.PcdType
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TimeOfDay
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.TypeOfCommunication
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.GetAdditionalFieldsInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.domain.usecase.PutAdditionalInfoUseCase
import br.com.mobicare.cielo.meuCadastroNovo.utils.AdditionalInfoUiState
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@ExperimentalCoroutinesApi
class UserAdditionalInfoChangeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val getAdditionalFieldsInfoUseCase: GetAdditionalFieldsInfoUseCase = mockk()
    private val putAdditionalInfoUseCase: PutAdditionalInfoUseCase = mockk()
    private lateinit var viewModel: UserAdditionalInfoChangeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockKAnnotations.init(this)
        viewModel = UserAdditionalInfoChangeViewModel(
            getAdditionalFieldsInfoUseCase,
            putAdditionalInfoUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getAdditionalFieldsInfoUseCase should emit GetSuccess when validation is successful`() =
        runTest {
            // Arrange
            val timeOfDay = arrayListOf(TimeOfDay("code", "description"))
            val typeOfCommunication = arrayListOf(TypeOfCommunication("code", "description"))
            val contactPreference = arrayListOf(ContactPreference("code", "description"))
            val pcdType = arrayListOf(PcdType("code", "description"))

            val response = GetAdditionalInfoFields(
                timeOfDay = timeOfDay,
                typeOfCommunication = typeOfCommunication,
                contactPreference = contactPreference,
                pcdType = pcdType
            )

            val successResult = CieloDataResult.Success(response)

            coEvery {
                getAdditionalFieldsInfoUseCase.invoke()
            } returns successResult

            // Act
            viewModel.getAdditionalInfoFields()

            // Assert
            viewModel.additionalFieldsInfoLiveData.value?.let {
                assert(it is AdditionalInfoUiState.GetSuccess && response == it.additionalFields)
            }
        }

    @Test
    fun `getAdditionalFieldsInfoUseCase should emit GetError when validation is failed`() =
        runTest {
            // Arrange
            val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                getAdditionalFieldsInfoUseCase.invoke()
            } returns errorResult

            // Act
            viewModel.getAdditionalInfoFields()

            // Assert
            viewModel.additionalFieldsInfoLiveData.value?.let {
                assert(it is AdditionalInfoUiState.GetError)
            }
        }

    @Test
    fun `putAdditionalInfoUseCase should emit UpdateSuccess when validation is successful`() =
        runTest {
            // Arrange
            val timeOfDay = "timeOfDay"
            val typeOfCommunication = arrayListOf("typeOfCommunication")
            val contactPreference = "contactPreference"
            val pcdType = "pcdType"

            val response = "Success"

            val successResult = CieloDataResult.Success(response)

            coEvery {
                putAdditionalInfoUseCase.invoke(any(), any(), any(), any())
            } returns successResult

            // Act
            viewModel.putAdditionalInfo(timeOfDay, typeOfCommunication, contactPreference, pcdType)

            // Assert
            viewModel.additionalFieldsInfoLiveData.value?.let {
                assert(it is AdditionalInfoUiState.UpdateSuccess)
            }
        }

    @Test
    fun `putAdditionalInfoUseCase should emit UpdateError when validation is failed`() =
        runTest {
            // Arrange
            val timeOfDay = "timeOfDay"
            val typeOfCommunication = arrayListOf("typeOfCommunication")
            val contactPreference = "contactPreference"
            val pcdType = "pcdType"
            val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

            coEvery {
                putAdditionalInfoUseCase.invoke(any(), any(), any(), any())
            } returns errorResult

            // Act
            viewModel.putAdditionalInfo(timeOfDay, typeOfCommunication, contactPreference, pcdType)

            // Assert
            viewModel.additionalFieldsInfoLiveData.value?.let {
                assert(it is AdditionalInfoUiState.UpdateError)
            }
        }
}