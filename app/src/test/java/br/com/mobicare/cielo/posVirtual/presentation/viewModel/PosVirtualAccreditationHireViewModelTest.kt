package br.com.mobicare.cielo.posVirtual.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BS_CONFIRM_TERM
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BS_CONFIRM_TERM_PIX
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BS_CONFIRM_TERM_TAP
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_REQUIRED_DATA_FIELD
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualAccreditationBanksUseCase
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateOrderUseCase
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire.PosVirtualAccreditationHireViewModel
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.PosVirtualAccreditationOfferViewModel
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INVALID_BANK
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.OfferResponseFactory.agreements
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.OfferResponseFactory.products
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.itemsConfigurations
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.offerID
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.sessionID
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.solutions
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory.solutionsNull
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationCreateOrderState
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationState
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
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualAccreditationHireViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getPosVirtualAccreditationBanksUseCase =
        mockk<GetPosVirtualAccreditationBanksUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()
    private val postPosVirtualAccreditationCreateOfferUseCase =
        mockk<PostPosVirtualCreateOrderUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val context = mockk<Context>()

    private lateinit var viewModel: PosVirtualAccreditationHireViewModel

    private val resultUserObjSuccess = CieloDataResult.Success(UserObj())
    private val resultCreateOrderSuccess = CieloDataResult.Success(offerID)
    private val resultEmpty = CieloDataResult.Empty()
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultErrorCodeInvalidBank = CieloDataResult.APIError(
        CieloAPIException(
            httpStatusCode = HTTP_UNKNOWN,
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(flagErrorCode = POS_VIRTUAL_ERROR_CODE_INVALID_BANK)
        )
    )
    private val resultMfaTokenError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
            newErrorMessage = NewErrorMessage(flagErrorCode = Text.OTP)
        )
    )

    @Before
    fun setup() {
        viewModel = PosVirtualAccreditationHireViewModel(
            getPosVirtualAccreditationBanksUseCase,
            postPosVirtualAccreditationCreateOfferUseCase,
            getUserObjUseCase,
            getFeatureTogglePreferenceUseCase,
        )

        coEvery { getUserObjUseCase() } returns resultUserObjSuccess

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @Test
    fun `it should set loadingBanksState as Success when success get banks and agreements not empty`() =
        runTest {
            val listBanks = solutions?.solutions?.first()?.banks

            coEvery {
                getPosVirtualAccreditationBanksUseCase()
            } returns if (listBanks != null) CieloDataResult.Success(listBanks) else resultError

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_TAP)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_PIX)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_REQUIRED_DATA_FIELD)
            } returns CieloDataResult.Success(true)

            val states = viewModel.loadingBanksState.captureValues()

            viewModel.start(offerID, sessionID, agreements, products, itemsConfigurations, null)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.Success::class.java)
        }

    @Test
    fun `it should set loadingBanksState as GenericError when error get banks and agreements not empty`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationBanksUseCase()
            } returns resultError

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_TAP)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_PIX)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_REQUIRED_DATA_FIELD)
            } returns CieloDataResult.Success(true)

            val states = viewModel.loadingBanksState.captureValues()

            viewModel.start(offerID, sessionID, agreements, products, itemsConfigurations, null)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.GenericError::class.java)
        }

    @Test
    fun `it should set loadingBanksState as GenericError when empty get banks and agreements not empty`() =
        runTest {
            coEvery {
                getPosVirtualAccreditationBanksUseCase()
            } returns resultEmpty

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_TAP)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_PIX)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_REQUIRED_DATA_FIELD)
            } returns CieloDataResult.Success(true)

            val states = viewModel.loadingBanksState.captureValues()

            viewModel.start(offerID, sessionID, agreements, products, itemsConfigurations, null)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.GenericError::class.java)
        }

    @Test
    fun `it should set loadingBanksState as GenericError when success get banks and agreements is empty`() =
        runTest {
            val listBanks = solutionsNull?.solutions?.first()?.banks

            coEvery {
                getPosVirtualAccreditationBanksUseCase()
            } returns if (listBanks != null) CieloDataResult.Success(listBanks) else resultError

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_TAP)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_BS_CONFIRM_TERM_PIX)
            } returns CieloDataResult.Success(true)

            coEvery {
                getFeatureTogglePreferenceUseCase(POS_VIRTUAL_REQUIRED_DATA_FIELD)
            } returns CieloDataResult.Success(true)

            val states = viewModel.loadingBanksState.captureValues()

            viewModel.start(offerID, sessionID, emptyList(), products, itemsConfigurations, null)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationState.ShowLoading::class.java)
            assertThat(states[ONE]).isInstanceOf(UIPosVirtualAccreditationState.HideLoading::class.java)
            assertThat(states[TWO]).isInstanceOf(UIPosVirtualAccreditationState.GenericError::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as Success when success create order`() =
        runTest {
            coEvery {
                postPosVirtualAccreditationCreateOfferUseCase(
                    any(),
                    any()
                )
            } returns resultCreateOrderSuccess

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.createOrder(DEFAULT_OTP)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.Success::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as InvalidBankError when create order with errorCode = INVALID_BANK`() =
        runTest {
            coEvery {
                postPosVirtualAccreditationCreateOfferUseCase(
                    any(),
                    any()
                )
            } returns resultErrorCodeInvalidBank

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.createOrder(DEFAULT_OTP)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.InvalidBankError::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as TokenError when create order with error token`() =
        runTest {
            coEvery {
                postPosVirtualAccreditationCreateOfferUseCase(
                    any(),
                    any()
                )
            } returns resultMfaTokenError

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.createOrder(DEFAULT_OTP)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.TokenError::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as GenericError when create order with error generic`() =
        runTest {
            coEvery {
                postPosVirtualAccreditationCreateOfferUseCase(
                    any(),
                    any()
                )
            } returns resultError

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.createOrder(DEFAULT_OTP)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.GenericError::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as GenericError when create order with empty`() =
        runTest {
            coEvery {
                postPosVirtualAccreditationCreateOfferUseCase(
                    any(),
                    any()
                )
            } returns resultEmpty

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.createOrder(DEFAULT_OTP)

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.GenericError::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as GenerateOTPCode when required not null and isEnabledRequiredDataField true`() =
        runTest {
            val required: Field =
                PosVirtualAccreditationHireViewModel::class.java.getDeclaredField("_required")
            val isEnabledRequiredDataField: Field =
                PosVirtualAccreditationHireViewModel::class.java.getDeclaredField("_isEnabledRequiredDataField")

            required.isAccessible = true
            required.set(viewModel, PosVirtualFactory.OfferResponseFactory.required)

            isEnabledRequiredDataField.isAccessible = true
            isEnabledRequiredDataField.set(viewModel, true)

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.toHire()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.OpenRequiredDataField::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as GenerateOTPCode when required not null and isEnabledRequiredDataField false`() =
        runTest {
            val required: Field =
                PosVirtualAccreditationHireViewModel::class.java.getDeclaredField("_required")
            val isEnabledRequiredDataField: Field =
                PosVirtualAccreditationHireViewModel::class.java.getDeclaredField("_isEnabledRequiredDataField")

            required.isAccessible = true
            required.set(viewModel, PosVirtualFactory.OfferResponseFactory.required)

            isEnabledRequiredDataField.isAccessible = true
            isEnabledRequiredDataField.set(viewModel, false)

            isEnabledRequiredDataField.isAccessible = true

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.toHire()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.GenerateOTPCode::class.java)
        }

    @Test
    fun `it should set loadingCreateOrderState as GenerateOTPCode when required null`() =
        runTest {
            val required: Field =
                PosVirtualAccreditationHireViewModel::class.java.getDeclaredField("_required")

            required.isAccessible = true
            required.set(viewModel, null)

            val states = viewModel.loadingCreateOrderState.captureValues()

            viewModel.toHire()

            dispatcherRule.advanceUntilIdle()

            assertThat(states[ZERO]).isInstanceOf(UIPosVirtualAccreditationCreateOrderState.GenerateOTPCode::class.java)
        }

}