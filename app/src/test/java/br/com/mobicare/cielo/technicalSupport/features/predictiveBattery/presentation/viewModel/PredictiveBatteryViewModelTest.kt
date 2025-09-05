package br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.viewModel

import android.content.Context
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.technicalSupport.domain.useCase.PostChangeBatteryUseCase
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryConstants.PREDICTIVE_BATTERY_PARAM_URL_DEEP_LINK
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryFactory
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryFactory.deepLinkModel
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryFactory.deepLinkModelWithInvalidLogicalNumber
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryFactory.equipmentID
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.UIPredictiveBatteryState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PredictiveBatteryViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postChangeBatteryUseCase = mockk<PostChangeBatteryUseCase>()
    private val context = mockk<Context>()

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultPredictiveBatteryPostChangeBatterySuccess = CieloDataResult.Success(
        PredictiveBatteryFactory.batteryResponseSuccess
    )
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultEmpty = CieloDataResult.Empty()

    private lateinit var viewModel: PredictiveBatteryViewModel

    @Before
    fun setup() {
        viewModel = PredictiveBatteryViewModel(
            getFeatureTogglePreferenceUseCase,
            getUserObjUseCase,
            postChangeBatteryUseCase,
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set uiState as ServiceAvailable when FT PREDICTIVE_BATTERY is true and logicNumber is valid and start with deeplink model`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(true)

            viewModel.start(deepLinkModel)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.ServiceAvailable::class.java)
        }

    @Test
    fun `it should set uiState as UnavailableService when FT PREDICTIVE_BATTERY is false and start with deeplink model`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(false)

            viewModel.start(deepLinkModel)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.UnavailableService::class.java)
        }

    @Test
    fun `it should set uiState as ValidateLogicNumberError when FT PREDICTIVE_BATTERY is true and logicNumber is invalid and start with deeplink model`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(true)

            viewModel.start(deepLinkModelWithInvalidLogicalNumber)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.ValidateLogicNumberError::class.java)
        }

    @Test
    fun `it should set uiState as ServiceAvailable when FT PREDICTIVE_BATTERY is true and logicNumber is valid and start with String`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(true)

            viewModel.start(equipmentID)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.ServiceAvailable::class.java)
        }

    @Test
    fun `it should set uiState as UnavailableService when FT PREDICTIVE_BATTERY is false and start with String`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(false)

            viewModel.start(equipmentID)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.UnavailableService::class.java)
        }

    @Test
    fun `it should set uiState as ValidateLogicNumberError when FT PREDICTIVE_BATTERY is true and logicNumber is invalid and start with emtpy String`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(true)

            viewModel.start(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.ValidateLogicNumberError::class.java)
        }

    @Test
    fun `it should set uiState as ValidateLogicNumberError when FT PREDICTIVE_BATTERY is true and logicNumber is invalid and start with null object`() =
        runTest {
            coEvery {
                getFeatureTogglePreferenceUseCase(FeatureTogglePreference.PREDICTIVE_BATTERY)
            } returns CieloDataResult.Success(true)

            viewModel.start(null)

            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.uiState.value).isInstanceOf(UIPredictiveBatteryState.ValidateLogicNumberError::class.java)
        }

    @Test
    fun `it should set uiState as SuccessRequestExchange when success postChangeBattery with changeBattery is true`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

            val states = viewModel.uiState.captureValues()

            viewModel.requestExchange(PredictiveBatteryFactory.phoneNumber)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.SuccessRequestExchange::class.java)
        }

    @Test
    fun `it should set uiState as RequestExchangeError when error postChangeBattery with changeBattery is true`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultError

            val states = viewModel.uiState.captureValues()

            viewModel.requestExchange(PredictiveBatteryFactory.phoneNumber)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.RequestExchangeError::class.java)
        }

    @Test
    fun `it should set uiState as RequestExchangeError when empty error postChangeBattery with changeBattery is true`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultEmpty

            val states = viewModel.uiState.captureValues()

            viewModel.requestExchange(PredictiveBatteryFactory.phoneNumber)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.RequestExchangeError::class.java)
        }

    @Test
    fun `it should set uiState as SuccessRefuseExchange when success postChangeBattery with changeBattery is false`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultPredictiveBatteryPostChangeBatterySuccess

            val states = viewModel.uiState.captureValues()

            viewModel.refuseExchange()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.SuccessRefuseExchange::class.java)
        }

    @Test
    fun `it should set uiState as RefuseExchangeError when error postChangeBattery with changeBattery is false`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultError

            val states = viewModel.uiState.captureValues()

            viewModel.refuseExchange()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.RefuseExchangeError::class.java)
        }

    @Test
    fun `it should set uiState as RefuseExchangeError when empty error postChangeBattery with changeBattery is false`() =
        runTest {
            coEvery { postChangeBatteryUseCase(any()) } returns resultEmpty

            val states = viewModel.uiState.captureValues()

            viewModel.refuseExchange()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPredictiveBatteryState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPredictiveBatteryState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPredictiveBatteryState.RefuseExchangeError::class.java)
        }

}