package br.com.mobicare.cielo.pixMVVM.presentation.key

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferBanksUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixTransferBanksUiState
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixBankAccountKeyViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val getPixTransferBanksUseCase = mockk<GetPixTransferBanksUseCase>()

    private val entity = PixTransactionsFactory.TransferBanks.entity
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()
    private val successResult = CieloDataResult.Success(entity)

    private lateinit var viewModel: PixBankAccountKeyViewModel
    private lateinit var states: List<PixTransferBanksUiState?>

    @Before
    fun setUp() {
        viewModel = PixBankAccountKeyViewModel(getUserObjUseCase, getPixTransferBanksUseCase)
        states = viewModel.transferBanksUiState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    private fun assertLoadingState(state: PixTransferBanksUiState?) {
        assertThat(state).isInstanceOf(PixTransferBanksUiState.Loading::class.java)
    }

    @Test
    fun `it should set PixTransferBanksUiState_Success on getTransferBanks call`() = runTest {
        // given
        coEvery { getPixTransferBanksUseCase() } returns successResult

        // when
        viewModel.getTransferBanks()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixTransferBanksUiState.Success::class.java)
    }

    @Test
    fun `it should set PixTransferBanksUiState_Error on getTransferBanks call for error result`() = runTest {
        // given
        coEvery { getPixTransferBanksUseCase() } returns errorResult

        // when
        viewModel.getTransferBanks()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixTransferBanksUiState.Error::class.java)
    }

    @Test
    fun `it should set the correct error state on getTransferBanks call depending of reload attempts`() = runTest {
        // given
        coEvery { getPixTransferBanksUseCase() } returns errorResult

        // when
        viewModel.run {
            getTransferBanks()
            getTransferBanks()
            getTransferBanks()
            getTransferBanks()
        }

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1])
            .isInstanceOf(PixTransferBanksUiState.UnableToFetchBankListError::class.java)

        assertLoadingState(states[2])
        assertThat(states[3])
            .isInstanceOf(PixTransferBanksUiState.UnableToFetchBankListError::class.java)

        assertLoadingState(states[4])
        assertThat(states[5])
            .isInstanceOf(PixTransferBanksUiState.UnableToFetchBankListError::class.java)

        assertLoadingState(states[6])
        assertThat(states[7])
            .isInstanceOf(PixTransferBanksUiState.UnavailableServiceError::class.java)
    }

    @Test
    fun `it should set PixTransferBanksUiState_Error on getTransferBanks call for empty result`() = runTest {
        // given
        coEvery { getPixTransferBanksUseCase() } returns emptyResult

        // when
        viewModel.getTransferBanks()

        // then
        dispatcherRule.advanceUntilIdle()

        assertLoadingState(states[0])
        assertThat(states[1]).isInstanceOf(PixTransferBanksUiState.Error::class.java)
    }

}

