package br.com.mobicare.cielo.pixMVVM.presentation.status

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAuthorizationStatusUseCase
import br.com.mobicare.cielo.pixMVVM.utils.PixAuthorizationStatusFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixAuthorizationStatusViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getPixAuthorizationStatusUseCase = mockk<GetPixAuthorizationStatusUseCase>()

    private val pixAuthorizationStatus = PixAuthorizationStatusFactory.entityWithPendingStatus
    private val entityWithActiveStatus = pixAuthorizationStatus.copy(status = PixStatus.ACTIVE)
    private val entityWithWaitingActivationStatus = pixAuthorizationStatus.copy(status = PixStatus.WAITING_ACTIVATION)
    private val entityWithPendingStatus = pixAuthorizationStatus.copy(status = PixStatus.PENDING)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val resultSuccess = CieloDataResult.Success(pixAuthorizationStatus)

    private lateinit var viewModel: PixAuthorizationStatusViewModel
    private lateinit var states: List<PixAuthorizationStatusUiState?>

    @Before
    fun setUp() {
        viewModel = PixAuthorizationStatusViewModel(
            getPixAuthorizationStatusUseCase
        )
        states = viewModel.uiState.captureValues()
    }

    private fun assertLoadingState(state: PixAuthorizationStatusUiState?) {
        assertThat(state)
            .isInstanceOf(PixAuthorizationStatusUiState.Loading::class.java)
    }

    @Test
    fun `it should set success state when getPixAuthorizationStatus call returns success result`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns resultSuccess

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.Success::class.java)
    }

    @Test
    fun `it should set active state when success result status is Active on getPixAuthorizationStatus call`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns CieloDataResult.Success(entityWithActiveStatus)

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.Active::class.java)
    }

    @Test
    fun `it should set waiting activation state when success result status is WaitingActivation on getPixAuthorizationStatus call`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns CieloDataResult.Success(entityWithWaitingActivationStatus)

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.WaitingActivation::class.java)
    }

    @Test
    fun `it should set pending state when success result status is Pending on getPixAuthorizationStatus call`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns CieloDataResult.Success(entityWithPendingStatus)

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.Pending::class.java)
    }

    @Test
    fun `it should set error state when getPixAuthorizationStatus call return error result`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns resultError

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.Error::class.java)
    }

    @Test
    fun `it should set error state when getPixAuthorizationStatus call returns empty result`() = runTest {
        // given
        coEvery { getPixAuthorizationStatusUseCase() } returns CieloDataResult.Empty()

        // when
        viewModel.getPixAuthorizationStatus()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixAuthorizationStatusUiState.Error::class.java)
    }

}

