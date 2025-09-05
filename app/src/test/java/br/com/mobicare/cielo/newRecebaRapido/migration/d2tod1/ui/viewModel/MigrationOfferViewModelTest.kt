package br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_200
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationConfirmationState
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.state.UiMigrationOfferState
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
class MigrationOfferViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val postContractUseCase = mockk<PostContractUseCase>()

    private lateinit var viewModel: MigrationOfferViewModel

    private val offerMock = HiringOffers(
        id = 1,
        name = "mockName",
        creditRateBefore = 1.0,
        creditRateAfter = 0.0,
        rateInstallmentsBefore = 1.0,
        rateInstallmentsAfter = 0.0
    )

    @Before
    fun setUp() {
        viewModel = MigrationOfferViewModel(getUserObjUseCase, postContractUseCase)
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
    fun `postContract with successful response updates state to Accept Success when user accept`(): Unit =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns
                CieloDataResult.Empty(
                    HTTP_STATUS_200,
                )
            viewModel.updateMigrationOfferState(offerMock)

            val uiState = viewModel.migrationConfirmationState.captureValues()
            viewModel.postContractUserDecision(true)

            dispatcherRule.advanceUntilIdle()
            assertThat(uiState[0]).isEqualTo(UiMigrationConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMigrationConfirmationState.HideLoading)
            assertThat(uiState[2]).isEqualTo(UiMigrationConfirmationState.AcceptSuccess)
        }

    @Test
    fun `postContract when response was not 200 updates state to Error`() =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns CieloDataResult.Empty(204)
            viewModel.updateMigrationOfferState(offerMock)

            val uiState = viewModel.migrationConfirmationState.captureValues()
            viewModel.postContractUserDecision(true)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("000000001", 1, true) }
            assertThat(uiState[0]).isEqualTo(UiMigrationConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMigrationConfirmationState.HideLoading)
            assertThat(uiState[2]).isInstanceOf(UiMigrationConfirmationState.Error::class.java)
        }

    @Test
    fun `postContract with unsuccessful response updates state to Error`(): Unit =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns resultError
            viewModel.updateMigrationOfferState(offerMock)

            val uiState = viewModel.migrationConfirmationState.captureValues()
            viewModel.postContractUserDecision(true)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("000000001", 1, true) wasNot called }
            assertThat(uiState[0]).isEqualTo(UiMigrationConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMigrationConfirmationState.HideLoading)
            assertThat(uiState[2]).isInstanceOf(UiMigrationConfirmationState.Error::class.java)
        }

    @Test
    fun `postContract with successful response updates state to Reject Success when use reject `(): Unit =
        runBlocking {
            coEvery { postContractUseCase.invoke(any(), any(), any()) } returns
                CieloDataResult.Empty(
                    HTTP_STATUS_200,
                )
            viewModel.updateMigrationOfferState(offerMock)

            val uiState = viewModel.migrationConfirmationState.captureValues()
            viewModel.postContractUserDecision(false)

            dispatcherRule.advanceUntilIdle()
            coVerify { postContractUseCase.invoke("000000001", 1, false) }
            assertThat(uiState[0]).isEqualTo(UiMigrationConfirmationState.ShowLoading)
            assertThat(uiState[1]).isEqualTo(UiMigrationConfirmationState.HideLoading)
            assertThat(uiState[2]).isEqualTo(UiMigrationConfirmationState.RejectSuccess)
        }

    @Test
    fun `initial state is correct`() =
        runBlocking {
            assertThat(viewModel.migrationConfirmationState.value).isEqualTo(null)
        }

    @Test
    fun `offer with both rates should update to both state`() =
        runBlocking {
            viewModel.updateMigrationOfferState(offerMock)

            assertThat(viewModel.migrationOfferState.value).isInstanceOf(UiMigrationOfferState.Both::class.java)
            assertThat((viewModel.migrationOfferState.value as UiMigrationOfferState.Both).creditOffer).isEqualTo(1.0)
            assertThat((viewModel.migrationOfferState.value as UiMigrationOfferState.Both).installmentOffer).isEqualTo(1.0)
        }

    @Test
    fun `offer with only credit rate should update to credit state`() =
        runBlocking {
            viewModel.updateMigrationOfferState(offerMock.copy(rateInstallmentsBefore = 0.0))

            assertThat(viewModel.migrationOfferState.value).isInstanceOf(UiMigrationOfferState.Credit::class.java)
            assertThat((viewModel.migrationOfferState.value as UiMigrationOfferState.Credit).creditOffer).isEqualTo(1.0)

        }

    @Test
    fun `offer with only installment rate should update to installment state`() =
        runBlocking {
            viewModel.updateMigrationOfferState(offerMock.copy(creditRateBefore = 0.0))

            assertThat(viewModel.migrationOfferState.value).isInstanceOf(UiMigrationOfferState.Installment::class.java)
            assertThat((viewModel.migrationOfferState.value as UiMigrationOfferState.Installment).installmentOffer).isEqualTo(1.0)
        }

    @Test
    fun `offer with no rates should update to noOfferError state`() =
        runBlocking {
            viewModel.updateMigrationOfferState(offerMock.copy(creditRateBefore = 0.0, rateInstallmentsBefore = 0.0))

            assertThat(viewModel.migrationOfferState.value).isInstanceOf(UiMigrationOfferState.NoOfferError::class.java)
        }
}
