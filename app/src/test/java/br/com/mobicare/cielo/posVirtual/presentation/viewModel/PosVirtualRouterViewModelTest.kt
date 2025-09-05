package br.com.mobicare.cielo.posVirtual.presentation.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import br.com.mobicare.cielo.posVirtual.domain.model.PosVirtual
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualEligibilityUseCase
import br.com.mobicare.cielo.posVirtual.presentation.router.PosVirtualRouterViewModel
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualFactory
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualRouterState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PosVirtualRouterViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)

    private val getUserViewHistoryUseCase = mockk<GetUserViewHistoryUseCase>()
    private val getPosVirtualEligibilityUseCase = mockk<GetPosVirtualEligibilityUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private lateinit var viewModel: PosVirtualRouterViewModel
    private lateinit var routerState: List<UIPosVirtualRouterState?>

    private val posVirtualEntity = PosVirtualFactory.Eligibility.posVirtualEntity

    private val resultSuccessWithSuccessStatus = CieloDataResult.Success(
        posVirtualEntity.copy(status = PosVirtualStatus.SUCCESS)
    )
    private val resultSuccessWithImpersonateRequired = CieloDataResult.Success(
        posVirtualEntity.copy(status = PosVirtualStatus.SUCCESS, impersonateRequired = true)
    )
    private val resultSuccessWithPendingStatus = CieloDataResult.Success(
        posVirtualEntity.copy(status = PosVirtualStatus.PENDING)
    )
    private val resultSuccessWithFailedStatus = CieloDataResult.Success(
        posVirtualEntity.copy(status = PosVirtualStatus.FAILED)
    )
    private val resultSuccessWithCanceledStatus = CieloDataResult.Success(
        posVirtualEntity.copy(status = PosVirtualStatus.CANCELED)
    )
    private val resultSuccessWithNotValidStatus = CieloDataResult.Success(
        posVirtualEntity.copy(status = null)
    )
    private val resultSuccessGetUserObj = CieloDataResult.Success(UserObj())

    private val resultProductNotFoundError = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(
                httpCode = 404,
                flagErrorCode = "PRODUCT_NOT_FOUND"
            )
        )
    )
    private val resultErrorCode420 = CieloDataResult.APIError(
        CieloAPIException(
            actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
            newErrorMessage = NewErrorMessage(
                httpCode = 420,
                message = "Error 420 with some message..."
            )
        )
    )

    private val resultEmpty = CieloDataResult.Empty()

    private val featureTogglePosVirtual404True = true
    private val viewedOnBoardingTrue = true
    private val viewedOnBoardingFalse = false

    @Before
    fun setup() {
        viewModel = PosVirtualRouterViewModel(
            getUserViewHistoryUseCase,
            getPosVirtualEligibilityUseCase,
            getFeatureTogglePreferenceUseCase,
            getUserObjUseCase
        )

        routerState = viewModel.routerState.captureValues()

        coEvery { getUserObjUseCase() } returns resultSuccessGetUserObj
    }

    private fun assertLoadingState(states: List<UIPosVirtualRouterState?>) {
        assertThat(states[0]).isInstanceOf(UIPosVirtualRouterState.Loading::class.java)
        assertThat((states[0] as UIPosVirtualRouterState.Loading).isLoading).isTrue()

        assertThat(states[1]).isInstanceOf(UIPosVirtualRouterState.Loading::class.java)
        assertThat((states[1] as UIPosVirtualRouterState.Loading).isLoading).isFalse()
    }

    private fun mockAndRunGetEligibilityTest(mockResult: CieloDataResult<PosVirtual>) {
        // given
        coEvery { getPosVirtualEligibilityUseCase() } returns mockResult

        // when
        viewModel.getEligibility(context)

        // then
        dispatcherRule.advanceUntilIdle()
    }

    @Test
    fun `it should set data on success result of getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithImpersonateRequired)

        assertThat(viewModel.data).isEqualTo(resultSuccessWithImpersonateRequired.value)
    }

    @Test
    fun `it should set status success state when success result comes with success status on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithSuccessStatus)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.StatusSuccess::class.java)
    }

    @Test
    fun `it should set impersonate required state when success result comes with impersonateRequired true on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithImpersonateRequired)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.ImpersonateRequired::class.java)
    }

    @Test
    fun `it should set status pending state when success result comes with pending status on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithPendingStatus)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.StatusPending::class.java)
    }

    @Test
    fun `it should set status canceled state when success result comes with canceled status on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithCanceledStatus)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.StatusCanceled::class.java)
    }

    @Test
    fun `it should set status failed state when success result comes with failed status on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithFailedStatus)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.StatusFailed::class.java)
    }

    @Test
    fun `it should set generic error state when success result comes with an invalid status on getEligibility call`() = runTest {
        mockAndRunGetEligibilityTest(resultSuccessWithNotValidStatus)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.GenericError::class.java)
    }

    @Test
    fun `it should set generic error state when result of getEligibility call is empty`() = runTest {
        mockAndRunGetEligibilityTest(resultEmpty)

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.GenericError::class.java)
    }

   /* @Test
    fun `it should set accreditation required state on error result of getEligibility call`() = runTest {
        // given
        coEvery { getPosVirtualEligibilityUseCase() } returns resultProductNotFoundError
        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns CieloDataResult.Success(featureTogglePosVirtual404True)
        coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(viewedOnBoardingTrue)

        // when
        viewModel.getEligibility(context)

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.AccreditationRequired::class.java)
    }*/

    @Test
    fun `it should set onboarding required state if it has not been seen on error result of getEligibility call`() = runTest {
        // given
        coEvery { getPosVirtualEligibilityUseCase() } returns resultProductNotFoundError
        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns CieloDataResult.Success(featureTogglePosVirtual404True)
        coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(viewedOnBoardingFalse)

        // when
        viewModel.getEligibility(context)

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.OnBoardingRequired::class.java)
    }

    @Test
    fun `it should set generic error state with same message as error result with code 420 on getEligibility call`() = runTest {
        // given
        coEvery { getPosVirtualEligibilityUseCase() } returns resultErrorCode420

        // when
        viewModel.getEligibility(context)

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(routerState)
        assertThat(routerState[2]).isInstanceOf(UIPosVirtualRouterState.GenericError::class.java)
        assertThat((routerState[2] as UIPosVirtualRouterState.GenericError).message)
            .isEqualTo(resultErrorCode420.apiException.newErrorMessage.message)
    }

}