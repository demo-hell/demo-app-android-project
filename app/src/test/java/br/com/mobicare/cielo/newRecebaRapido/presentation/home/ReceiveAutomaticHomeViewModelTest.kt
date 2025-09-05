package br.com.mobicare.cielo.newRecebaRapido.presentation.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.listOffer
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.resultWhenClientHasNoOffer
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.resultWhenClientHasNoOffers
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.resultWhenClientHasRRContracted
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOffers
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
class ReceiveAutomaticHomeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getReceiveAutomaticOffersUseCase = mockk<GetReceiveAutomaticOffersUseCase>(relaxed = true)
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val resultError = ReceiveAutomaticFactory.resultError

    private val context = mockk<Context>()

    private lateinit var viewModel: ReceiveAutomaticHomeViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel = ReceiveAutomaticHomeViewModel(
            getReceiveAutomaticOffersUseCase,
            getUserObjUseCase
        )
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set UiStateRAOffers as Success state`() = runTest {
        // given
        coEvery { getReceiveAutomaticOffersUseCase(any()) } returns CieloDataResult.Success(listOffer)

        // when
        viewModel.getReceiveAutomaticOffers()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.receiveAutomaticOffersMutableLiveData.value is UiStateRAOffers.Success)
    }

    @Test
    fun `it should set error state on getReceiveAutomaticOffersUseCase call result`() = runTest {
        // given
        coEvery { getReceiveAutomaticOffersUseCase(any()) } returns resultError

        // when
        viewModel.getReceiveAutomaticOffers()

        // then
        dispatcherRule.advanceUntilIdle()

        assert(viewModel.receiveAutomaticOffersMutableLiveData.value is UiStateRAOffers.Error)
    }

    @Test
    fun `it should set UiStateRAOffers as Error state when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.getReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersMutableLiveData.value is UiStateRAOffers.Error)
        }

    @Test
    fun `it should set UiArvHomeState as HiredOfferExists`() = runTest {
        // given
        coEvery { getReceiveAutomaticOffersUseCase(any()) } returns resultWhenClientHasRRContracted
        val uiState = viewModel.receiveAutomaticOffersMutableLiveData.captureValues()

        // when
        viewModel.getReceiveAutomaticOffers()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState.last()).isEqualTo(UiStateRAOffers.HiredOfferExists)
    }

    @Test
    fun `it should set UiArvHomeState as OfferNotFound`() = runTest {
        // given
        coEvery { getReceiveAutomaticOffersUseCase(any()) } returns resultWhenClientHasNoOffer
        val uiState = viewModel.receiveAutomaticOffersMutableLiveData.captureValues()

        // when
        viewModel.getReceiveAutomaticOffers()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState.last()).isEqualTo(UiStateRAOffers.OffersNotFound)
    }

    @Test
    fun `it should set UiArvHomeState as OffersNotFound`() = runTest {
        // given
        coEvery { getReceiveAutomaticOffersUseCase(any()) } returns resultWhenClientHasNoOffers
        val uiState = viewModel.receiveAutomaticOffersMutableLiveData.captureValues()

        // when
        viewModel.getReceiveAutomaticOffers()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState.last()).isEqualTo(UiStateRAOffers.OffersNotFound)
    }

}