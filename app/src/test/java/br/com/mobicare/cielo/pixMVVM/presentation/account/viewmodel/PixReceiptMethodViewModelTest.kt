package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetOnBoardingFulfillmentUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixReceiptMethodUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixOnBoardingFactory
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
class PixReceiptMethodViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getOnBoardingFulfillmentUseCase = mockk<GetOnBoardingFulfillmentUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    private val entity = PixOnBoardingFactory.onBoardingFulfillmentEntity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()
    private val successResult = CieloDataResult.Success(entity)
    private val successResultFT = CieloDataResult.Success(true)

    private lateinit var viewModel: PixReceiptMethodViewModel
    private lateinit var states: List<PixReceiptMethodUiState?>

    @Before
    fun setUp() {
        viewModel =
            PixReceiptMethodViewModel(
                getUserObjUseCase,
                getOnBoardingFulfillmentUseCase,
                getFeatureTogglePreferenceUseCase,
            )
        states = viewModel.uiState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
        coEvery { getFeatureTogglePreferenceUseCase(any()) } returns successResultFT
    }

    private fun assertLoadingState(state: PixReceiptMethodUiState?) {
        assertThat(state).isInstanceOf(PixReceiptMethodUiState.Loading::class.java)
    }

    @Test
    fun `it should set PixReceiptMethodUiState_Success on getOnBoardingFulfillmentUseCase call`() = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns successResult

        // when
        viewModel.getOnBoardingFulfillment()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixReceiptMethodUiState.Success::class.java)
    }

    private fun mockAndRunActiveReceiptMethodAssertion(
        onBoardingFulfillment: OnBoardingFulfillment,
        expectedReceiptMethod: PixReceiptMethod
    ) = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns CieloDataResult.Success(onBoardingFulfillment)

        // when
        viewModel.getOnBoardingFulfillment()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.onBoardingFulfillment).isEqualTo(onBoardingFulfillment)
        assertThat(viewModel.activeReceiptMethod).isEqualTo(expectedReceiptMethod)
    }

    @Test
    fun `it should set activeMethodReceipt = CIELO_ACCOUNT`() = runTest {
        val onBoardingFulfillment = entity.copy(
            profileType = ProfileType.FREE_MOVEMENT,
        )

        mockAndRunActiveReceiptMethodAssertion(onBoardingFulfillment, PixReceiptMethod.CIELO_ACCOUNT)
    }

    @Test
    fun `it should set activeMethodReceipt = TRANSFER_BY_SALE`() = runTest {
        val onBoardingFulfillment = entity.copy(
            profileType = ProfileType.AUTOMATIC_TRANSFER,
            settlementScheduled = OnBoardingFulfillment.SettlementScheduled(
                isEnabled = false
            )
        )

        mockAndRunActiveReceiptMethodAssertion(onBoardingFulfillment, PixReceiptMethod.TRANSFER_BY_SALE)
    }

    @Test
    fun `it should set activeMethodReceipt = SCHEDULED_TRANSFER`() = runTest {
        val onBoardingFulfillment = entity.copy(
            profileType = ProfileType.AUTOMATIC_TRANSFER,
            settlementScheduled = OnBoardingFulfillment.SettlementScheduled(
                isEnabled = true
            )
        )

        mockAndRunActiveReceiptMethodAssertion(onBoardingFulfillment, PixReceiptMethod.SCHEDULED_TRANSFER)
    }

    @Test
    fun `it should set PixReceiptMethodUiState_Error on getOnBoardingFulfillmentUseCase call`() = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns errorResult

        // when
        viewModel.getOnBoardingFulfillment()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixReceiptMethodUiState.Error::class.java)
    }

    @Test
    fun `it should set PixReceiptMethodUiState_Error on getOnBoardingFulfillmentUseCase call when result is empty`() = runTest {
        // given
        coEvery { getOnBoardingFulfillmentUseCase() } returns emptyResult

        // when
        viewModel.getOnBoardingFulfillment()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixReceiptMethodUiState.Error::class.java)
    }

}

