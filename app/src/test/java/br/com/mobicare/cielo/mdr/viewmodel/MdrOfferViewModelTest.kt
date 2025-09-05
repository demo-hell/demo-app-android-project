package br.com.mobicare.cielo.mdr.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_200
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase
import br.com.mobicare.cielo.mdr.ui.MdrOfferViewModel
import br.com.mobicare.cielo.mdr.ui.state.UiMdrConfirmationState
import br.com.mobicare.cielo.mdr.ui.state.UiMdrOfferState
import br.com.mobicare.cielo.mdr.utils.Constants
import com.google.common.truth.Truth.assertThat
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MdrOfferViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postContractUseCase = mockk<PostContractUseCase>()

    private lateinit var viewModel: MdrOfferViewModel

    @Before
    fun setUp() {
        viewModel = MdrOfferViewModel(getUserObjUseCase, postContractUseCase)
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    @Test
    fun `postContract with successful response updates state to Accept Success when user accept`() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns
                CieloDataResult.Empty(
                    HTTP_STATUS_200,
                )

            val uiState = viewModel.mdrConfirmationState.captureValues()
            viewModel.postContractUserDecision("154654", 1, true)

            dispatcherRule.advanceUntilIdle()
            assertThat(uiState[0]).isEqualTo(UiMdrConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMdrConfirmationState.HideLoading)
            assertThat(uiState[2]).isEqualTo(UiMdrConfirmationState.AcceptSuccess)
        }

    @Test
    fun `postContract when response was not 200 updates state to Error`() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns CieloDataResult.Empty(204)

            val uiState = viewModel.mdrConfirmationState.captureValues()
            viewModel.postContractUserDecision("154654", 1, true)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("154654", 1, true) }
            assertThat(uiState[0]).isEqualTo(UiMdrConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMdrConfirmationState.HideLoading)
            assertThat(uiState[2]).isInstanceOf(UiMdrConfirmationState.Error::class.java)
        }

    @Test
    fun `postContract with unsuccessful response updates state to Error`() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns resultError

            val uiState = viewModel.mdrConfirmationState.captureValues()
            viewModel.postContractUserDecision("15454", 1, true)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("15454", 1, true) wasNot called }
            assertThat(uiState[0]).isEqualTo(UiMdrConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMdrConfirmationState.HideLoading)
            assertThat(uiState[2]).isInstanceOf(UiMdrConfirmationState.Error::class.java)
        }

    @Test
    fun `postContract with null bannerId uses default value`() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns
                CieloDataResult.Empty(
                    HTTP_STATUS_200,
                )

            val uiState = viewModel.mdrConfirmationState.captureValues()
            viewModel.postContractUserDecision(null, null, true)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke(EMPTY, ZERO, true) }
            assertThat(uiState[0]).isEqualTo(UiMdrConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMdrConfirmationState.HideLoading)
            assertThat(uiState[2]).isEqualTo(UiMdrConfirmationState.AcceptSuccess)
        }

    @Test
    fun `postContract with successful response updates state to Reject Success when use reject `() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns
                CieloDataResult.Empty(
                    HTTP_STATUS_200,
                )

            val uiState = viewModel.mdrConfirmationState.captureValues()
            viewModel.postContractUserDecision("154654", 1, false)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("154654", 1, false) }
            assertThat(uiState[0]).isEqualTo(UiMdrConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMdrConfirmationState.HideLoading)
            assertThat(uiState[2]).isEqualTo(UiMdrConfirmationState.RejectSuccess)
        }

    @Test
    fun `initial state is correct`() =
        runBlocking {
            assertThat(viewModel.mdrConfirmationState.value).isEqualTo(null)
        }

    @Test
    fun `offer with REATIVACAO_MDR_ALUGUEL updates state to ShowPostponedWithoutRR`() =
        runBlocking {
            val offerId = Constants.REATIVACAO_MDR_ALUGUEL

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowPostponedWithoutRR)
        }

    @Test
    fun `offer with REATIVACAO_MDR_RR_ALUGUEL updates state to ShowPostponedWithRR`() =
        runBlocking {
            val offerId = Constants.REATIVACAO_MDR_RR_ALUGUEL

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowPostponedWithRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_ALUGUEL updates state to ShowPostponedWithoutRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_ALUGUEL

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowPostponedWithoutRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_RR_ALUGUEL updates state to ShowPostponedWithRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_RR_ALUGUEL

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowPostponedWithRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_ALUGUEL_RR_S_POSTECIPADO updates state to ShowWithoutPostponedWithRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_ALUGUEL_RR_S_POSTECIPADO

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowWithoutPostponedWithRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_ALUGUEL_S_POSTECIPADO updates state to ShowWithoutPostponedWithoutRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_ALUGUEL_S_POSTECIPADO

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowWithoutPostponedWithoutRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_S_MAQUINA updates state to ShowWithoutEquipmentWithoutRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_S_MAQUINA

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowWithoutEquipmentWithoutRR)
        }

    @Test
    fun `offer with RETENCAO_MDR_RR_S_MAQUINA updates state to ShowWithoutEquipmentWithRR`() =
        runBlocking {
            val offerId = Constants.RETENCAO_MDR_RR_S_MAQUINA

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.ShowWithoutEquipmentWithRR)
        }

    @Test
    fun `offer with invalid offerId updates state to error`() =
        runBlocking {
            val offerId = null

            viewModel.updateMdrOfferState(offerId)

            assertThat(viewModel.mdrOfferState.value).isEqualTo(UiMdrOfferState.Error)
        }
}
