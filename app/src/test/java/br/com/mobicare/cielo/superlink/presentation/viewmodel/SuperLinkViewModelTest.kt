package br.com.mobicare.cielo.superlink.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetAccessTokenUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.superlink.domain.usecase.CheckPaymentLinkActiveUseCase
import br.com.mobicare.cielo.superlink.utils.SuperLinkFactory
import br.com.mobicare.cielo.superlink.utils.UiSuperLinkState
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SuperLinkViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val checkPaymentLinkActiveUseCase = mockk<CheckPaymentLinkActiveUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getAccessTokenUseCase = mockk<GetAccessTokenUseCase>()

    private val paymentLinkResponse = SuperLinkFactory.paymentLinkResponseForActiveCheck
    private val userObj = SuperLinkFactory.userObjWithEc
    private val accessToken = SuperLinkFactory.accessToken
    private val emptyAccessToken = SuperLinkFactory.emptyAccessToken
    private val apiExceptionError400 = SuperLinkFactory.apiExceptionError400

    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val successResult = CieloDataResult.Success(paymentLinkResponse)
    private val successAccessTokenResult = CieloDataResult.Success(accessToken)
    private val successGetUserObjResult = CieloDataResult.Success(userObj)

    private lateinit var viewModel: SuperLinkViewModel

    @Before
    fun setUp() {
        viewModel = SuperLinkViewModel(
            checkPaymentLinkActiveUseCase,
            getUserObjUseCase,
            getAccessTokenUseCase
        )
    }

    @Test
    fun `it should set loading state before any other state on isPaymentLinkActive call`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult

        val states = viewModel.paymentLinkActiveState.captureValues()

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(states[0])
            .isInstanceOf(UiSuperLinkState.Loading::class.java)
    }

    @Test
    fun `it should set success state when isPaymentLinkActive call returns success result`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult
        coEvery { getAccessTokenUseCase() } returns successAccessTokenResult
        coEvery { checkPaymentLinkActiveUseCase() } returns successResult

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.Success::class.java)
    }

    @Test
    fun `it should set error state when token is empty on isPaymentLinkActive call result`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult
        coEvery { getAccessTokenUseCase() } returns CieloDataResult.Success(emptyAccessToken)
        coEvery { checkPaymentLinkActiveUseCase() } returns successResult

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.Error::class.java)
    }

    @Test
    fun `it should set error state when EC is null on isPaymentLinkActive call result`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns errorResult
        coEvery { getAccessTokenUseCase() } returns successAccessTokenResult
        coEvery { checkPaymentLinkActiveUseCase() } returns successResult

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.Error::class.java)
    }

    @Test
    fun `it should set error state when isPaymentLinkActive call returns empty result`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult
        coEvery { getAccessTokenUseCase() } returns successAccessTokenResult
        coEvery { checkPaymentLinkActiveUseCase() } returns CieloDataResult.Empty()

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.Error::class.java)
    }

    @Test
    fun `it should set error state when isPaymentLinkActive call returns error result with http code 500`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult
        coEvery { getAccessTokenUseCase() } returns successAccessTokenResult
        coEvery { checkPaymentLinkActiveUseCase() } returns errorResult

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.Error::class.java)
    }

    @Test
    fun `it should set error not eligible state when isPaymentLinkActive call returns error result with http code 400`() = runTest {
        // given
        coEvery { getUserObjUseCase() } returns successGetUserObjResult
        coEvery { getAccessTokenUseCase() } returns successAccessTokenResult
        coEvery { checkPaymentLinkActiveUseCase() } returns CieloDataResult.APIError(apiExceptionError400)

        // when
        viewModel.isPaymentLinkActive()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.paymentLinkActiveState.value)
            .isInstanceOf(UiSuperLinkState.ErrorNotEligible::class.java)
    }

}

