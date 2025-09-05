package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.enums.DaysOfWeek
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.ContractReceiveAutomaticOfferUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory
import br.com.mobicare.cielo.newRecebaRapido.util.ReceiveAutomaticFactory.listOffer
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOContract
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReceiveAutomaticConfirmationViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getReceiveAutomaticOffersUseCase = mockk<GetReceiveAutomaticOffersUseCase>()
    private val contractReceiveAutomaticOfferUseCase = mockk<ContractReceiveAutomaticOfferUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()
    private val resultError = ReceiveAutomaticFactory.resultError

    private val context = mockk<Context>()

    private lateinit var viewModel: ReceiveAutomaticConfirmationViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel = ReceiveAutomaticConfirmationViewModel(
            contractReceiveAutomaticOfferUseCase,
            getReceiveAutomaticOffersUseCase,
            getUserObjUseCase
        )
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
    }

    @Test
    fun `it should set UiStateRAODetailsOffers as Success state`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any(), true) } returns CieloDataResult.Success(
                listOffer
            )

            // when
            viewModel.getPostValidityOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersPostValidityLiveData.value is UiStateRAODetailsOffers.Success)
        }

    @Test
    fun `it should set error state on getReceiveAutomaticOffersUseCase call result`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any(), true) } returns resultError

            // when
            viewModel.getPostValidityOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersPostValidityLiveData.value is UiStateRAODetailsOffers.Error)
        }

    @Test
    fun `it should set UiStateRAODetailsOffers as Error state when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticOffersUseCase(any(), true) } returns CieloDataResult.Empty()

            // when
            viewModel.getPostValidityOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersPostValidityLiveData.value is UiStateRAODetailsOffers.Error)
        }

    @Test
    fun `it should set UiStateRAOContract as Success state when the CieloDataResult is empty`() =
        runTest {
            // given
            coEvery { contractReceiveAutomaticOfferUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.contractingReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            assert(viewModel.receiveAutomaticOffersDetailsContractLiveData.value is UiStateRAOContract.Empty)
        }

    @Test
    fun `it should request contract RA with only required parameters on daily periodicity`() =
        runTest {
            // given
            coEvery { contractReceiveAutomaticOfferUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.periodicitySelected = ConstantsReceiveAutomatic.DAILY
            viewModel.typeTransactionSelected = ConstantsReceiveAutomatic.BOTH
            viewModel.contractingReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            coVerify { contractReceiveAutomaticOfferUseCase.invoke(params =
            ReceiveAutomaticContractRequest(
                settlementTerm = null,
                dayOfTheWeek = null,
                customFastRepayPeriodicity = ConstantsReceiveAutomatic.DAILY,
                customFastRepayContractType = ConstantsReceiveAutomatic.BOTH
            )
            )}
        }

    @Test
    fun `it should request contract RA with only required parameters on monthly periodicity`() =
        runTest {
            // given
            coEvery { contractReceiveAutomaticOfferUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.periodicitySelected = ConstantsReceiveAutomatic.MONTHLY
            viewModel.monthDaySelected = ONE
            viewModel.typeTransactionSelected = ConstantsReceiveAutomatic.BOTH
            viewModel.contractingReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            coVerify { contractReceiveAutomaticOfferUseCase(
                ReceiveAutomaticContractRequest(
                    settlementTerm = ONE,
                    dayOfTheWeek = null,
                    customFastRepayPeriodicity = ConstantsReceiveAutomatic.MONTHLY,
                    customFastRepayContractType = ConstantsReceiveAutomatic.BOTH
                )
            )}
        }

    @Test
    fun `it should request contract RA with only required parameters on weekly periodicity`() =
        runTest {
            // given
            coEvery { contractReceiveAutomaticOfferUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.periodicitySelected = ConstantsReceiveAutomatic.WEEKLY
            viewModel.weekDaySelected = DaysOfWeek.TUESDAY.day
            viewModel.typeTransactionSelected = ConstantsReceiveAutomatic.BOTH
            viewModel.contractingReceiveAutomaticOffers()

            // then
            dispatcherRule.advanceUntilIdle()

            coVerify { contractReceiveAutomaticOfferUseCase(
                ReceiveAutomaticContractRequest(
                    settlementTerm = null,
                    dayOfTheWeek = DaysOfWeek.TUESDAY.day,
                    customFastRepayPeriodicity = ConstantsReceiveAutomatic.WEEKLY,
                    customFastRepayContractType = ConstantsReceiveAutomatic.BOTH
                )
            )}
        }
}