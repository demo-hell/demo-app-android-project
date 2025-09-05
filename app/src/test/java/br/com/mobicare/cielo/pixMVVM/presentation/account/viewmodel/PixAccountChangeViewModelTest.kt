package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

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
import br.com.mobicare.cielo.pixMVVM.domain.usecase.ChangePixProfileUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.UpdatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixProfileUiState
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixScheduledTransferUiState
import br.com.mobicare.cielo.pixMVVM.utils.PixScheduledSettlementFactory
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixAccountChangeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val changePixProfileUseCase = mockk<ChangePixProfileUseCase>()
    private val createPixScheduledSettlementUseCase = mockk<CreatePixScheduledSettlementUseCase>()
    private val updatePixScheduledSettlementUseCase = mockk<UpdatePixScheduledSettlementUseCase>()

    private val scheduledSettlementResponse = PixScheduledSettlementFactory.pixScheduledSettlementResponse
    private val errorResult = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))
    private val emptyResult = CieloDataResult.Empty()

    private lateinit var viewModel: PixAccountChangeViewModel
    private lateinit var profileStates: List<PixProfileUiState?>
    private lateinit var scheduledTransferStates: List<PixScheduledTransferUiState?>

    @Before
    fun setUp() {
        viewModel = PixAccountChangeViewModel(
            getUserObjUseCase,
            changePixProfileUseCase,
            createPixScheduledSettlementUseCase,
            updatePixScheduledSettlementUseCase
        )
        profileStates = viewModel.profileState.captureValues()
        scheduledTransferStates = viewModel.scheduledTransferState.captureValues()

        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    // ========================
    // changeProfile
    // ========================

    @Test
    fun `it should set PixProfileUiState_Success on changeProfile call`() = runTest {
        // given
        coEvery { changePixProfileUseCase(any()) } returns CieloDataResult.Success(EMPTY)

        // when
        viewModel.changeProfile(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(profileStates[0]).isInstanceOf(PixProfileUiState.Success::class.java)
    }

    @Test
    fun `it should set PixProfileUiState_Error on changeProfile call when result is error`() = runTest {
        // given
        coEvery { changePixProfileUseCase(any()) } returns errorResult

        // when
        viewModel.changeProfile(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(profileStates[0]).isInstanceOf(PixProfileUiState.Error::class.java)
    }

    @Test
    fun `it should set PixProfileUiState_Error on changeProfile call when result is empty`() = runTest {
        // given
        coEvery { changePixProfileUseCase(any()) } returns emptyResult

        // when
        viewModel.changeProfile(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(profileStates[0]).isInstanceOf(PixProfileUiState.Error::class.java)
    }

    // ========================
    // toggleScheduledTransfer
    // ========================

    @Test
    fun `it should set PixScheduledTransferUiState_Success on toggleScheduledTransfer call`() = runTest {
        // given
        coEvery { createPixScheduledSettlementUseCase(any()) } returns CieloDataResult.Success(scheduledSettlementResponse)

        // when
        viewModel.toggleScheduledTransfer(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Success::class.java)

        assertThat((scheduledTransferStates[0] as PixScheduledTransferUiState.Success).response)
            .isEqualTo(scheduledSettlementResponse)
    }

    @Test
    fun `it should set PixScheduledTransferUiState_Error on toggleScheduledTransfer call when result is error`() = runTest {
        // given
        coEvery { createPixScheduledSettlementUseCase(any()) } returns errorResult

        // when
        viewModel.toggleScheduledTransfer(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Error::class.java)
    }

    @Test
    fun `it should set PixScheduledTransferUiState_Error on toggleScheduledTransfer call when result is empty`() = runTest {
        // given
        coEvery { createPixScheduledSettlementUseCase(any()) } returns emptyResult

        // when
        viewModel.toggleScheduledTransfer(EMPTY, true)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Error::class.java)
    }

    // ========================
    // updateScheduledTransfer
    // ========================

    @Test
    fun `it should set PixScheduledTransferUiState_Success on updateScheduledTransfer call`() = runTest {
        // given
        coEvery { updatePixScheduledSettlementUseCase(any()) } returns CieloDataResult.Success(scheduledSettlementResponse)

        // when
        viewModel.updateScheduledTransfer(EMPTY, emptyList())

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Success::class.java)

        assertThat((scheduledTransferStates[0] as PixScheduledTransferUiState.Success).response)
            .isEqualTo(scheduledSettlementResponse)
    }

    @Test
    fun `it should set PixScheduledTransferUiState_Error on updateScheduledTransfer call when result is error`() = runTest {
        // given
        coEvery { updatePixScheduledSettlementUseCase(any()) } returns errorResult

        // when
        viewModel.updateScheduledTransfer(EMPTY, emptyList())

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Error::class.java)
    }

    @Test
    fun `it should set PixScheduledTransferUiState_Error on updateScheduledTransfer call when result is empty`() = runTest {
        // given
        coEvery { updatePixScheduledSettlementUseCase(any()) } returns emptyResult

        // when
        viewModel.updateScheduledTransfer(EMPTY, emptyList())

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(scheduledTransferStates[0]).isInstanceOf(PixScheduledTransferUiState.Error::class.java)
    }

}

