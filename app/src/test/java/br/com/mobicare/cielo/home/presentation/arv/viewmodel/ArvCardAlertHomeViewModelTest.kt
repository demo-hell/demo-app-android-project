package br.com.mobicare.cielo.home.presentation.arv.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.utils.ArvFactory.arvScheduledAnticipationNotHired
import br.com.mobicare.cielo.arv.utils.ArvFactory.arvSingleAnticipationFromCardHomeFlow
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_420
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvCardAlertHomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getArvSingleAnticipationWithDateNewUseCase = mockk<GetArvSingleAnticipationWithDateNewUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()
    private val arvScheduledAnticipationUseCase = mockk<GetArvScheduledAnticipationUseCase>()

    private lateinit var viewModel: ArvCardAlertHomeViewModel

    @Before
    fun setUp() {
        viewModel =
            ArvCardAlertHomeViewModel(
                getArvSingleAnticipationWithDateNewUseCase,
                getFeatureTogglePreference,
                arvScheduledAnticipationUseCase,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `getArvCardInformation should return ShowArvCardAlert state when FeatureTogglePreference is enabled`() =
        runTest {
            // Arrange
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery {
                getArvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.Success(arvSingleAnticipationFromCardHomeFlow)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipationNotHired)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(
                UiArvCardAlertState.ShowArvCardAlert(
                    arvSingleAnticipationFromCardHomeFlow,
                ),
            )
        }

    @Test
    fun `getArvCardInformation should not return arvAnticipation when FeatureTogglePreference is disabled`() =
        runTest {
            // Arrange
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(false)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipationNotHired)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            coVerify(inverse = true) {
                getArvSingleAnticipationWithDateNewUseCase.invoke(any(), any(), any())
            }
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }

    @Test
    fun `getArvCardInformation should return Error state when get error and FeatureTogglePreference is enabled`() =
        runTest {
            // Arrange
            val error = CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery {
                getArvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.APIError(error)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipationNotHired)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.Error(error.newErrorMessage))
        }

    @Test
    fun `getArvCardInformation should return HideArvCardAlert state when get empty and FeatureTogglePreference is enabled`() =
        runTest {
            // Arrange
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery {
                getArvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.Empty()
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipationNotHired)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }

    @Test
    fun `getArvCardInformation should return HideArvCardAlert state when get error with code 420 and FeatureTogglePreference is enabled`() =
        runTest {
            // Arrange
            val error = CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR, httpStatusCode = HTTP_STATUS_420)
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery {
                getArvSingleAnticipationWithDateNewUseCase(any(), any(), any())
            } returns CieloDataResult.APIError(error)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipationNotHired)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }

    @Test
    fun `getArvCardInformation should return HideArvCardAlert state when schedule is true and root is false`() =
        runTest {
            // Arrange
            val arvScheduledAnticipation =
                arvScheduledAnticipationNotHired.copy(
                    rateSchedules =
                        listOf(
                            arvScheduledAnticipationNotHired.rateSchedules?.first()?.copy(
                                schedule = true,
                                cnpjRoot = false,
                            ),
                        ),
                )
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipation)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }

    @Test
    fun `getArvCardInformation should return HideArvCardAlert state when schedule is false and root is true`() =
        runTest {
            // Arrange
            val arvScheduledAnticipation =
                arvScheduledAnticipationNotHired.copy(
                    rateSchedules =
                        listOf(
                            arvScheduledAnticipationNotHired.rateSchedules?.first()?.copy(
                                schedule = false,
                                cnpjRoot = true,
                            ),
                        ),
                )
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipation)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }

    @Test
    fun `getArvCardInformation should return HideArvCardAlert state when schedule is true and root is true`() =
        runTest {
            // Arrange
            val arvScheduledAnticipation =
                arvScheduledAnticipationNotHired.copy(
                    rateSchedules =
                        listOf(
                            arvScheduledAnticipationNotHired.rateSchedules?.first()?.copy(
                                schedule = true,
                                cnpjRoot = true,
                            ),
                        ),
                )
            coEvery { getFeatureTogglePreference(any()) } returns CieloDataResult.Success(true)
            coEvery { arvScheduledAnticipationUseCase.invoke() } returns CieloDataResult.Success(arvScheduledAnticipation)

            // Act
            viewModel.getArvCardInformation()

            // Assert
            assertThat(viewModel.arvCardAlertLiveData.value).isEqualTo(UiArvCardAlertState.HideArvCardAlert)
        }
}
