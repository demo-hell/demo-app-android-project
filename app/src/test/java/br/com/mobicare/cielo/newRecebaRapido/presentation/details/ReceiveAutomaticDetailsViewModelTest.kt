package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.listOffer
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.offersToSummary
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
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
class ReceiveAutomaticDetailsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getReceiveAutomaticOffersUseCase = mockk<GetReceiveAutomaticOffersUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val resultError = ReceiveAutomaticFactory.resultError

    private val context = mockk<Context>()

    private lateinit var viewModel: ReceiveAutomaticDetailsViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel = ReceiveAutomaticDetailsViewModel(
            getReceiveAutomaticOffersUseCase,
            getUserObjUseCase
        )
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set UiStateRAODetailsOffers as Success state`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any()) } returns CieloDataResult.Success(
                listOffer
            )

            // when
            viewModel.getReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersDetailsLiveData.value is UiStateRAODetailsOffers.Success)
        }

    @Test
    fun `it should set error state on getReceiveAutomaticOffersUseCase call result`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any()) } returns resultError

            // when
            viewModel.getReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersDetailsLiveData.value is UiStateRAODetailsOffers.Error)
        }

    @Test
    fun `it should set UiStateRAODetailsOffers as Error state when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.getReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersDetailsLiveData.value is UiStateRAODetailsOffers.Error)
        }

    @Test
    fun `from offers list it should map correctly to offer summary`() =
        runTest {
            // given
            val listOffer = offersToSummary

            // when
            val summary = listOffer.mapToOfferSummary()

            // then
            assert(summary.size == 2)
            val visaOffer = summary.first()

            assert(visaOffer.cashFee == 5.06)
            assert(visaOffer.brandName == "VISA")
            assert(visaOffer.brandCode == 1)


            val visaInstallment1 = visaOffer.installments?.first()
            assert(visaInstallment1?.number == 2)
            assert(visaInstallment1?.fee == 6.93)

            val visaInstallment2 = visaOffer.installments?.get(1)
            assert(visaInstallment2?.number == 3)
            assert(visaInstallment2?.fee == 8.15)

            val masterOffer = summary[1]

            assert(masterOffer.cashFee == 5.06)
            assert(masterOffer.brandName == "MASTERCARD")
            assert(masterOffer.brandCode == 2)
            assert(masterOffer.installments?.size == 1)

            val masterInstallment1 = masterOffer.installments?.first()
            assert(masterInstallment1?.number == 2)
            assert(masterInstallment1?.fee == 6.93)
        }

}