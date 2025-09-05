package br.com.mobicare.cielo.arv.presentation.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.domain.useCase.GetArvAnticipationHistoryNewUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvOptInUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvWhatsAppContactData
import br.com.mobicare.cielo.arv.utils.ArvConstants.MERCHANT_NOT_ELIGIBLE
import br.com.mobicare.cielo.arv.utils.ArvFactory
import br.com.mobicare.cielo.arv.utils.ArvFactory.anticipation
import br.com.mobicare.cielo.arv.utils.ArvFactory.anticipationHistory
import br.com.mobicare.cielo.arv.utils.ArvFactory.meResponse
import br.com.mobicare.cielo.arv.utils.ArvFactory.meResponseWithoutCNPJNumber
import br.com.mobicare.cielo.arv.utils.ArvFactory.nullAnticipationHistory
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultAnticipationClosedMarket
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultAnticipationCorporateDesk
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultAnticipationNotEligible
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.arv.utils.OptInState
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.arv.utils.UiArvHomeState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvSingleState
import br.com.mobicare.cielo.arv.utils.UiArvTypeState
import br.com.mobicare.cielo.arv.utils.UiArvUserState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetConfigurationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.commons.utils.isRoot
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvHomeViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getArvOptInUseCase = mockk<GetArvOptInUseCase>()
    private val getMeInformationUseCase = mockk<GetMeInformationUseCase>()
    private val getArvAnticipationNewUseCase = mockk<GetArvSingleAnticipationWithDateNewUseCase>()
    private val getArvScheduledAnticipationUseCase = mockk<GetArvScheduledAnticipationUseCase>()
    private val getArvAnticipationHistoryNewUseCase = mockk<GetArvAnticipationHistoryNewUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getConfigurationUseCase = mockk<GetConfigurationUseCase>()

    private val context = mockk<Context>()

    private lateinit var viewModel: ArvHomeViewModel

    private val arvWhatsAppContactDataJson = ArvFactory.arvWhatsAppContactDataJson
    private val arvWhatsAppContactData = ArvFactory.arvWhatsAppContactData

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA) } returns
            CieloDataResult.Success(true)

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

        viewModel =
            ArvHomeViewModel(
                getArvOptInUseCase,
                getMeInformationUseCase,
                getArvAnticipationNewUseCase,
                getArvScheduledAnticipationUseCase,
                getArvAnticipationHistoryNewUseCase,
                getFeatureTogglePreference,
                getUserObjUseCase,
                getConfigurationUseCase,
            )

        setupSuccessResponses()
    }

    private fun setupSuccessResponses() {
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())

        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
            CieloDataResult.Success(true)
        coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
            CieloDataResult.Success(true)

        coEvery { getArvOptInUseCase() } returns
            CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

        coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
            CieloDataResult.Success(anticipationHistory)

        coEvery { getArvScheduledAnticipationUseCase() } returns
            CieloDataResult.Success(
                ArvFactory.arvScheduledAnticipation,
            )

        coEvery { getArvAnticipationNewUseCase(any(), any(), any()) } returns
            CieloDataResult.Success(anticipation)
    }

    @Test
    fun `given user is eligible should request opt-in`() =
        runTest {
            // given
            coEvery { getArvOptInUseCase.invoke() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInRequestResponse)

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvOptInStateLiveData.value)
                .isEqualTo(
                    OptInState.MissingOptIn,
                )
        }

    @Test
    fun `given user is not eligible should skip request opt-in`() =
        runTest {
            // given
            coEvery { getArvOptInUseCase.invoke() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvOptInStateLiveData.value)
                .isNull()
        }

    @Test
    fun `it should set UiArvTypeState as SetupAnticipationScheduled false`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            // when
            viewModel.getAnticipationScheduledFeatureToggle()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvScheduledAnticipationStateLiveData.value).isEqualTo(
                UiArvScheduledAnticipationState.DisabledScheduled,
            )
        }

    @Test
    fun `it should set UiArvTypeState as SetupAnticipationSingle true`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)
            // when
            viewModel.getAnticipationSingleFeatureToggle()

            // then
            dispatcherRule.advanceUntilIdle()
            assertThat(viewModel.arvAnticipationTypeLiveData.value)
                .isEqualTo(
                    UiArvTypeState.SetupAnticipationSingle(
                        true,
                        R.drawable.background_stroke_cloud_200_8dp_radius,
                    ),
                )
        }

    @Test
    fun `it should set UiArvHomeState as ErrorArvNegotiation`() =
        runTest {
            // given
            val error =
                NewErrorMessage(
                    actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR,
                )

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(true)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            coEvery { getArvScheduledAnticipationUseCase() } returns
                CieloDataResult.Success(
                    ArvFactory.arvScheduledAnticipation,
                )
            coEvery { getArvAnticipationNewUseCase(any(),any(), any()) } returns resultError

            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()
            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)
            assertThat(uiState[2]).isEqualTo(
                UiArvHomeState.ErrorArvNegotiation(message = R.string.anticipation_error, error),
            )
        }

    @Test
    fun `it should set UiArvHomeState as ClosedMarket`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            coEvery { getArvAnticipationNewUseCase(any(), any(), any()) } returns resultAnticipationClosedMarket
            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)
            assertThat(uiState[2]).isEqualTo(UiArvHomeState.ClosedMarket)
        }

    @Test
    fun `it should set UiArvHomeState as NotEligible`() =
        runTest {
            // given
            val error =
                NewErrorMessage(
                    flagErrorCode = MERCHANT_NOT_ELIGIBLE,
                    actionErrorType = ActionErrorTypeEnum.NETWORK_ERROR,
                    mfaErrorCode = "",
                )

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            coEvery { getArvAnticipationNewUseCase(any(), any(), any()) } returns resultAnticipationNotEligible
            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)
            assertThat(uiState[2]).isEqualTo(
                UiArvHomeState.NotEligible(
                    error,
                ),
            )
        }

    @Test
    fun `it should set UiArvHomeState as CorporateDesk`() =
        runTest {
            // given
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            coEvery { getArvAnticipationNewUseCase(any(), any(), any()) } returns resultAnticipationCorporateDesk
            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)
            assertThat(uiState[2]).isEqualTo(UiArvHomeState.CorporateDesk)
        }

    @Test
    fun `it should set UiArvHomeState as ErrorArvNegotiation when the CieloDataResult is empty`() =
        runTest {
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            // given
            coEvery { getArvAnticipationNewUseCase(any(), any(), any()) } returns CieloDataResult.Empty()
            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)

            assertThat(uiState[2]).isEqualTo(
                UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error),
            )
        }

    @Test
    fun `it should set UiArvHomeState as SuccessArvNegotiation`() =
        runTest {
            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA) } returns
                CieloDataResult.Success(false)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getFeatureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA) } returns
                CieloDataResult.Success(true)

            coEvery { getArvOptInUseCase() } returns
                CieloDataResult.Success(ArvFactory.ArvOptInDoneResponse)

            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(anticipationHistory)

            // given
            coEvery { getArvAnticipationNewUseCase(negotiationType = "CIELO", any(), any()) } returns
                CieloDataResult.Success(anticipation)

            coEvery { getArvAnticipationNewUseCase("MARKET", any(), any()) } returns
                CieloDataResult.Success(anticipation.copy(negotiationType = "MARKET"))

            val uiState = viewModel.arvHomeSingleAnticipationLiveData.captureValues()
            val uiCieloSingleState = viewModel.arvCieloAnticipationLiveData.captureValues()
            val uiMarketSingleState = viewModel.arvMarketAnticipationLiveData.captureValues()

            // when
            viewModel.getOptIn()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHomeState.ShowLoadingArvNegotiation)
            assertThat(uiState[1]).isEqualTo(UiArvHomeState.HideLoadingArvNegotiation)

            assertThat(uiCieloSingleState[0]).isEqualTo(
                UiArvSingleState.SuccessArvSingle(anticipation),
            )

            assertThat(
                (uiCieloSingleState[0] as UiArvSingleState.SuccessArvSingle)
                    .anticipation.negotiationType,
            ).isEqualTo("CIELO")

            assertThat(uiMarketSingleState[0]).isEqualTo(
                UiArvSingleState.SuccessArvSingle(anticipation.copy(negotiationType = "MARKET")),
            )
            assertThat(
                (uiMarketSingleState[0] as UiArvSingleState.SuccessArvSingle)
                    .anticipation.negotiationType,
            ).isEqualTo("MARKET")
        }

    @Test
    fun `it should set UiArvHomeState as ErrorMeInformation`() =
        runTest {
            // given
            coEvery { getMeInformationUseCase() } returns resultError
            val uiState = viewModel.arvUserState.captureValues()

            // when
            viewModel.getUserInformation()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvUserState.ShowLoadingMeInformation)
            assertThat(uiState[1]).isEqualTo(UiArvUserState.HideLoadingMeInformation)

            assertThat(uiState[2]).isEqualTo(
                UiArvUserState.ErrorMeInformation(R.string.anticipation_me_information_error),
            )
        }

    @Test
    fun `it should set UiArvHomeState as ErrorMeInformation when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getMeInformationUseCase() } returns CieloDataResult.Empty()
            val uiState = viewModel.arvUserState.captureValues()

            // when
            viewModel.getUserInformation()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvUserState.ShowLoadingMeInformation)
            assertThat(uiState[1]).isEqualTo(
                UiArvUserState.ErrorMeInformation(R.string.anticipation_me_information_error),
            )
        }

    @Test
    fun `it should set UiArvHomeState as ErrorMeInformation when the response is null`() =
        runTest {
            // given
            mockkStatic(::isRoot)
            every { isRoot() } returns true
            coEvery { getMeInformationUseCase() } returns
                CieloDataResult.Success(meResponseWithoutCNPJNumber)

            // when
            viewModel.getUserInformation()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(viewModel.arvUserState.value).isEqualTo(
                UiArvUserState.SuccessMeInformation(
                    name = meResponseWithoutCNPJNumber.activeMerchant.tradingName.orEmpty(),
                    numberEstablishment = meResponseWithoutCNPJNumber.activeMerchant.id,
                    cpnjEstablishment = EMPTY_VALUE,
                ),
            )
        }

    @Test
    fun `it should set UiArvHomeState as SuccessMeInformation`() =
        runTest {
            // given
            mockkStatic(::isRoot)
            every { isRoot() } returns true
            coEvery { getMeInformationUseCase() } returns CieloDataResult.Success(meResponse)

            // when
            viewModel.getUserInformation()

            // then
            dispatcherRule.advanceUntilIdle()
            assertThat(viewModel.arvUserState.value).isEqualTo(
                UiArvUserState.SuccessMeInformation(
                    name = meResponse.activeMerchant.tradingName.orEmpty(),
                    numberEstablishment = meResponse.activeMerchant.id,
                    cpnjEstablishment = "00.000.000/0001-00",
                ),
            )
        }

    @Test
    fun `it should set UiArvHistoricState as ErrorHistoric`() =
        runTest {
            // given
            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns resultError
            val uiState = viewModel.arvAnticipationHistoryLiveData.captureValues()

            // when
            viewModel.getArvAnticipationHistory()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
            assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)

            assertThat(uiState[2]).isEqualTo(
                UiArvHistoricState.ErrorHistoric(R.string.anticipation_historic_error),
            )
        }

    @Test
    fun `it should set UiArvHistoricState as EmptyHistoric when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns CieloDataResult.Empty()
            val uiState = viewModel.arvAnticipationHistoryLiveData.captureValues()

            // when
            viewModel.getArvAnticipationHistory()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
            assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
            assertThat(uiState[2]).isEqualTo(UiArvHistoricState.EmptyHistoric)
        }

    @Test
    fun `it should set UiArvHistoricState as EmptyHistoric when the CieloDataResult is Success and the response is null`() =
        runTest {
            // given
            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(
                    nullAnticipationHistory,
                )
            val uiState = viewModel.arvAnticipationHistoryLiveData.captureValues()

            // when
            viewModel.getArvAnticipationHistory()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
            assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
            assertThat(uiState[2]).isEqualTo(UiArvHistoricState.EmptyHistoric)
        }

    @Test
    fun `it should set UiArvHistoricState as SuccessHistoric when the CieloDataResult is Success and the response is not null`() =
        runTest {
            // given
            coEvery { getArvAnticipationHistoryNewUseCase(any()) } returns
                CieloDataResult.Success(
                    anticipationHistory,
                )
            val uiState = viewModel.arvAnticipationHistoryLiveData.captureValues()

            // when
            viewModel.getArvAnticipationHistory()

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
            assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
            assertThat(uiState[2]).isEqualTo(
                UiArvHistoricState.SuccessHistoric(
                    Item(
                        discountAmount = 2.0,
                        grossAmount = 12340.0,
                        modality = "test",
                        negotiationDate = "2022-03-24",
                    ),
                ),
            )
        }

    @Test
    fun `it should set ArvWhatsAppContactData from configuration data source`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase(any(), any()) } returns CieloDataResult.Success(arvWhatsAppContactDataJson)

            // when
            viewModel.fetchWhatsAppContactData()

            // then
            assertThat(viewModel.arvWhatsAppContactData.value).isEqualTo(arvWhatsAppContactData)
        }

    @Test
    fun `it should set the default ArvWhatsAppContactData when configuration is empty`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase(any(), any()) } returns CieloDataResult.Success(EMPTY_VALUE)

            // when
            viewModel.fetchWhatsAppContactData()

            // then
            assertThat(viewModel.arvWhatsAppContactData.value).isEqualTo(ArvWhatsAppContactData())
        }

    @Test
    fun `it should set the default ArvWhatsAppContactData when configuration is not found`() =
        runTest {
            // given
            coEvery { getConfigurationUseCase(any(), any()) } returns CieloDataResult.Empty()

            // when
            viewModel.fetchWhatsAppContactData()

            // then
            assertThat(viewModel.arvWhatsAppContactData.value).isEqualTo(ArvWhatsAppContactData())
        }
}
