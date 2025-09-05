package br.com.mobicare.cielo.pixMVVM.presentation.router

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.BlockType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetOnBoardingFulfillmentUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.router.utils.PixRouterUiState
import br.com.mobicare.cielo.pixMVVM.presentation.router.viewmodel.PixRouterViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixOnBoardingFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixRouterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getOnBoardingFulfillmentUseCase = mockk<GetOnBoardingFulfillmentUseCase>()
    private val featureTogglePreference = mockk<FeatureTogglePreference>()
    private val userPreferences = mockk<UserPreferences>()
    private val mfaRepository = mockk<MfaRepository>()

    private val onBoardingFulfillment = PixOnBoardingFactory.onBoardingFulfillmentEntity
    private val mfaEligibilityResponseWithActiveStatus = MfaEligibilityResponse(
        status = "ACTIVE",
        type = null,
        typeCode = null,
        statusCode = null,
        statusTrace = null
    )
    private val mfaEligibilityResponseWithNotActiveStatus = mfaEligibilityResponseWithActiveStatus.copy(status = EMPTY)
    private val ftShowMenuPixFalse = Feature(show = false)
    private val ftShowMenuPixTrue = Feature(show = true)

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()

    private lateinit var viewModel: PixRouterViewModel
    private lateinit var states: List<PixRouterUiState?>

    @Before
    fun setUp() {
        viewModel = PixRouterViewModel(
            getOnBoardingFulfillmentUseCase,
            featureTogglePreference,
            userPreferences,
            mfaRepository
        )

        states = viewModel.uiState.captureValues()

        coEvery { featureTogglePreference.getFeatureToggleObject(any()) } returns ftShowMenuPixTrue
    }

    private fun assertLoadingState(state: PixRouterUiState?) {
        assertThat(state).isInstanceOf(PixRouterUiState.Loading::class.java)
    }

    private fun runViewModel(showDataQuery: Boolean = false) {
        viewModel.run {
            setShowDataQuery(showDataQuery)
            getOnBoardingFulfillment()
        }
    }

    @Test
    fun `it should set Unavailable error state when showMenuPix feature toggle and showDataQuery flag are both false`() = runTest {
        // given
        coEvery { featureTogglePreference.getFeatureToggleObject(any()) } returns ftShowMenuPixFalse

        // when
        runViewModel(showDataQuery = false)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(states[0]).isInstanceOf(PixRouterUiState.Unavailable::class.java)
    }

    @Test
    fun `it should set Error state on error result of getOnBoardingFulfillmentUseCase call`() = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns errorResult

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.Error::class.java)
    }

    @Test
    fun `it should set Error state on empty result of getOnBoardingFulfillmentUseCase call`() = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns emptyResult

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.Error::class.java)
    }

    @Test
    fun `it should set NotEligible state when isEnabled and isEligible of OnBoardingFulfillment result are both false`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(isEligible = false, isEnabled = false)
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.NotEligible::class.java)
    }

    @Test
    fun `it should set AccreditationRequired state when isEligible is true and status is WAITING_ACTIVATION`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = false,
                isEligible = true,
                status = PixStatus.WAITING_ACTIVATION
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.AccreditationRequired::class.java)
    }

    @Test
    fun `it should set ShowAuthorizationStatus state when isEligible is true and status is not WAITING_ACTIVATION`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = false,
                isEligible = true,
                status = PixStatus.PENDING
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowAuthorizationStatus::class.java)
    }

    @Test
    fun `it should set ShowAuthorizationStatus state when isEnabled field and showDataQuery flag are both true`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(isEnabled = true)
        )

        // when
        runViewModel(showDataQuery = true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowAuthorizationStatus::class.java)
    }

    @Test
    fun `it should set ShowAuthorizationStatus state when isEnabled is true and status is not ACTIVE`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(isEnabled = true, status = PixStatus.PENDING)
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowAuthorizationStatus::class.java)
    }

    @Test
    fun `it should set ShowAuthorizationStatus state when isEnabled is true, status is ACTIVE and blockType is IN_PROGRESS`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                blockType = BlockType.IN_PROGRESS
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowAuthorizationStatus::class.java)
    }

    @Test
    fun `it should set BlockPennyDrop state when isEnabled is true, status is ACTIVE and blockType is BANK_DOMICILE`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                blockType = BlockType.BANK_DOMICILE
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.BlockPennyDrop::class.java)
    }

    @Test
    fun `it should set BlockPennyDrop state when isEnabled is true, status is ACTIVE and blockType is PENNY_DROP`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                blockType = BlockType.PENNY_DROP
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.BlockPennyDrop::class.java)
    }

    @Test
    fun `it should set Error state when isEnabled is true, status is ACTIVE and profileType is invalid or null`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                profileType = null
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.Error::class.java)
    }

    @Test
    fun `it should set EnablePixPartner state when isEnabled is true, status is ACTIVE and profileType is PARTNER_BANK`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                profileType = ProfileType.PARTNER_BANK
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.EnablePixPartner::class.java)
    }

    @Test
    fun `it should set ShowPixExtract state with correct data when profileType is LEGACY`() = runTest {
        // given
        val onBoardingFulfillmentLegacy = onBoardingFulfillment.copy(
            isEnabled = true,
            status = PixStatus.ACTIVE,
            profileType = ProfileType.LEGACY
        )

        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(onBoardingFulfillmentLegacy)

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowPixExtract::class.java)

        val actualState = states[1] as PixRouterUiState.ShowPixExtract

        assertThat(actualState.profileType)
            .isEqualTo(onBoardingFulfillmentLegacy.profileType)
        assertThat(actualState.pixAccount)
            .isEqualTo(onBoardingFulfillmentLegacy.pixAccount)
        assertThat(actualState.settlementScheduled)
            .isEqualTo(onBoardingFulfillmentLegacy.settlementScheduled)
    }

    @Test
    fun `it should set ShowPixExtract state with correct data when profileType is AUTOMATIC_TRANSFER`() = runTest {
        // given
        val onBoardingFulfillmentAuto = onBoardingFulfillment.copy(
            isEnabled = true,
            status = PixStatus.ACTIVE,
            profileType = ProfileType.AUTOMATIC_TRANSFER
        )

        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(onBoardingFulfillmentAuto)

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowPixExtract::class.java)

        val actualState = states[1] as PixRouterUiState.ShowPixExtract

        assertThat(actualState.profileType).isEqualTo(onBoardingFulfillmentAuto.profileType)
        assertThat(actualState.pixAccount).isEqualTo(onBoardingFulfillmentAuto.pixAccount)
        assertThat(actualState.settlementScheduled).isEqualTo(onBoardingFulfillmentAuto.settlementScheduled)
    }

    @Test
    fun `it should set Error state when profileType is invalid or null`() = runTest {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                profileType = null
            )
        )

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.Error::class.java)
    }

    private fun mockAndRunMfaEligibility(hasValidSeed: Boolean, isOnBoardingViewed: Boolean) {
        // given
        coEvery {
            getOnBoardingFulfillmentUseCase()
        } returns CieloDataResult.Success(
            onBoardingFulfillment.copy(
                isEnabled = true,
                status = PixStatus.ACTIVE,
                profileType = ProfileType.FREE_MOVEMENT
            )
        )
        coEvery { userPreferences.isPixOnboardingHomeViewed } returns isOnBoardingViewed
        coEvery { mfaRepository.hasValidSeed() } returns hasValidSeed

        // when
        runViewModel()

        // then
        dispatcherRule.advanceUntilIdle()
    }

    @Test
    fun `it should set TokenConfigurationRequired state with isOnBoardingViewed equals to false when profileType is FREE_MOVEMENT and mfaRepository has valid seed`() = runTest {
        mockAndRunMfaEligibility(hasValidSeed = true, isOnBoardingViewed = false)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.TokenConfigurationRequired::class.java)
        (states[1] as PixRouterUiState.TokenConfigurationRequired).let {
            assertThat(it.isOnBoardingViewed).isEqualTo(false)
            assertThat(it.pixAccount).isEqualTo(onBoardingFulfillment.pixAccount)
        }
    }

    @Test
    fun `it should set TokenConfigurationRequired state with isOnBoardingViewed equals to true when profileType is FREE_MOVEMENT and mfaRepository has valid seed`() = runTest {
        mockAndRunMfaEligibility(hasValidSeed = true, isOnBoardingViewed = true)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.TokenConfigurationRequired::class.java)
        (states[1] as PixRouterUiState.TokenConfigurationRequired).let {
            assertThat(it.isOnBoardingViewed).isEqualTo(true)
            assertThat(it.pixAccount).isEqualTo(onBoardingFulfillment.pixAccount)
        }
    }

    @Test
    fun `it should set TokenConfigurationRequired state when profileType is FREE_MOVEMENT and response of MFA checkEligibility is not ACTIVE`() = runTest {
        val slot = slot<APICallbackDefault<MfaEligibilityResponse, String>>()

        coEvery {
            mfaRepository.checkEligibility(capture(slot))
        } answers {
            slot.captured.onSuccess(mfaEligibilityResponseWithNotActiveStatus)
        }

        mockAndRunMfaEligibility(hasValidSeed = false, isOnBoardingViewed = true)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.TokenConfigurationRequired::class.java)
        (states[1] as PixRouterUiState.TokenConfigurationRequired).let {
            assertThat(it.isOnBoardingViewed).isEqualTo(true)
            assertThat(it.pixAccount).isEqualTo(onBoardingFulfillment.pixAccount)
        }
    }

    @Test
    fun `it should set MfaEligibilityError state when profileType is FREE_MOVEMENT and MFA checkEligibility callback is an error`() = runTest {
        val slot = slot<APICallbackDefault<MfaEligibilityResponse, String>>()

        coEvery {
            mfaRepository.checkEligibility(capture(slot))
        } answers {
            slot.captured.onError(ErrorMessage())
        }

        mockAndRunMfaEligibility(hasValidSeed = false, isOnBoardingViewed = true)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.MfaEligibilityError::class.java)
    }

    @Test
    fun `it should set OnBoardingRequired state when profileType is FREE_MOVEMENT, isOnBoardingViewed is false and response of MFA checkEligibility is ACTIVE`() = runTest {
        val slot = slot<APICallbackDefault<MfaEligibilityResponse, String>>()

        coEvery {
            mfaRepository.checkEligibility(capture(slot))
        } answers {
            slot.captured.onSuccess(mfaEligibilityResponseWithActiveStatus)
        }

        mockAndRunMfaEligibility(hasValidSeed = false, isOnBoardingViewed = false)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.OnBoardingRequired::class.java)

        val actualState = states[1] as PixRouterUiState.OnBoardingRequired

        assertThat(actualState.pixAccount).isEqualTo(onBoardingFulfillment.pixAccount)
    }

    @Test
    fun `it should set ShowPixHome state when profileType is FREE_MOVEMENT, isOnBoardingViewed is true and response of MFA checkEligibility is ACTIVE`() = runTest {
        val slot = slot<APICallbackDefault<MfaEligibilityResponse, String>>()

        coEvery {
            mfaRepository.checkEligibility(capture(slot))
        } answers {
            slot.captured.onSuccess(mfaEligibilityResponseWithActiveStatus)
        }

        mockAndRunMfaEligibility(hasValidSeed = false, isOnBoardingViewed = true)

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixRouterUiState.ShowPixHome::class.java)

        val actualState = states[1] as PixRouterUiState.ShowPixHome

        assertThat(actualState.pixAccount).isEqualTo(onBoardingFulfillment.pixAccount)
    }

}