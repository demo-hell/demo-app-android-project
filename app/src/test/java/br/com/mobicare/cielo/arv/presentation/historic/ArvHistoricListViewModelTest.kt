package br.com.mobicare.cielo.arv.presentation.historic

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.useCase.GetArvAnticipationHistoryNewUseCase
import br.com.mobicare.cielo.arv.presentation.historic.list.ArvHistoricListViewModel
import br.com.mobicare.cielo.arv.utils.ArvFactory.anticipationHistory
import br.com.mobicare.cielo.arv.utils.ArvFactory.nullAnticipationHistory
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
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
class ArvHistoricListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var dispatcherRule = TestDispatcherRule()

    private val getArvAnticipationHistoricUseCase = mockk<GetArvAnticipationHistoryNewUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private lateinit var viewModel: ArvHistoricListViewModel

    private val context = mockk<Context>()

    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel = ArvHistoricListViewModel(
                getArvAnticipationHistoricUseCase,
                getUserObjUseCase
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set ArvHistoricUiState as Success`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns CieloDataResult.Success(
                anticipationHistory
        )

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = false, isStart = true)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
        assertThat(uiState[2]).isEqualTo(UiArvHistoricState.Success)
    }

    @Test
    fun `it should set ArvHistoricUiState as Success when isMoreHistoric is true`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns CieloDataResult.Success(
                anticipationHistory
        )

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = true, isStart = false)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingMoreHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingMoreHistoric)
    }

    @Test
    fun `it should set ArvHistoricUiState as EmptyHistoric when the CieloDataResult is Success and the response is null and isMoreHistoric is false`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns CieloDataResult.Success(
                nullAnticipationHistory
        )

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = false, isStart = false)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
        assertThat(uiState[2]).isEqualTo(UiArvHistoricState.EmptyHistoric)
    }

    @Test
    fun `it should set ArvHistoricUiState as EmptyHistoric when the CieloDataResult is Success and the response is null and isMoreHistoric is true`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns CieloDataResult.Success(
                nullAnticipationHistory
        )

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = true, isStart = false)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingMoreHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingMoreHistoric)
    }

    @Test
    fun `it should set ArvHistoricUiState as ErrorHistoric when isMoreHistoric is false`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns resultError

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = false, isStart = false)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingHistoric)
        assertThat(uiState[2]).isEqualTo(UiArvHistoricState.ErrorHistoric(R.string.anticipation_historic_error, resultError.apiException.newErrorMessage))
    }

    @Test
    fun `it should set ArvHistoricUiState as ErrorHistoric when isMoreHistoric is true`() = runTest {
        coEvery { getArvAnticipationHistoricUseCase(any()) } returns resultError

        val uiState = viewModel.arvHistoricUiState.captureValues()
        viewModel.getHistoric(isMoreHistoric = true, isStart = false)
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvHistoricState.ShowLoadingMoreHistoric)
        assertThat(uiState[1]).isEqualTo(UiArvHistoricState.HideLoadingMoreHistoric)
        assertThat(uiState[2]).isEqualTo(UiArvHistoricState.ErrorHistoric(R.string.anticipation_historic_error, resultError.apiException.newErrorMessage))
    }

}