package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.domain.usecase.ApproveConsentUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetDetainerUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GetUserCardBalanceUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.RejectConsentUseCase
import br.com.mobicare.cielo.openFinance.presentation.resume.ResumePaymentHolderViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateApproveConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateMerchantList
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateRejectConsent
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateResumeDetainer
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.coVerifyAll
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.verifyAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResumeDetainerViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseDetainer = mockk<GetDetainerUseCase>()
    private val useCaseUserCardBalance = mockk<GetUserCardBalanceUseCase>()
    private val useCaseApproveConsent = mockk<ApproveConsentUseCase>()
    private val useCaseRejectConsent = mockk<RejectConsentUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)

    private val successUserCardBalance = OpenFinanceFactory.successResponseUserCardBalance
    private val successApproveConsent = OpenFinanceFactory.successResponseConsent
    private val successRejectConsent = OpenFinanceFactory.successResponseConsent

    private val resultSuccessUserCardBalance = CieloDataResult.Success(successUserCardBalance)
    private val resultSuccessApproveConsent = CieloDataResult.Success(successApproveConsent)
    private val resultSuccessRejectConsent = CieloDataResult.Success(successRejectConsent)
    private lateinit var viewModel: ResumePaymentHolderViewModel

    @Before
    fun setUp() {
        viewModel = ResumePaymentHolderViewModel(
            useCaseDetainer,
            useCaseUserCardBalance,
            useCaseApproveConsent,
            useCaseRejectConsent,
            userPreferences
        )
    }

    @Test
    fun `it should set the success state in the account balance call success result`() =
        runTest {

            coEvery { useCaseUserCardBalance(EMPTY) } returns resultSuccessUserCardBalance

            viewModel.getCardsBalance(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assertEquals(
                viewModel.getCardsBalanceLiveData.value,
                OpenFinanceFactory.successResponseUserCardBalance
            )
        }

    @Test
    fun `it should set the success state in the consent approval call success result`() =
        runTest {

            coEvery {
                useCaseApproveConsent(
                    OpenFinanceFactory.consentIdRequest,
                    EMPTY
                )
            } returns resultSuccessApproveConsent

            viewModel.approveConsent(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.approveConsentLiveData.value is UIStateApproveConsent.Success)
        }

    @Test
    fun `it should set the error state in the error result of the consent approval call`() =
        runTest {

            coEvery {
                useCaseApproveConsent(
                    OpenFinanceFactory.consentIdRequest,
                    EMPTY
                )
            } returns OpenFinanceFactory.resultError

            viewModel.approveConsent(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.approveConsentLiveData.value is UIStateApproveConsent.ErrorPaymentInProgress)
        }

    @Test
    fun `it should set the success state in the call success result of consent refusal`() =
        runTest {

            coEvery {
                useCaseRejectConsent(
                    OpenFinanceFactory.rejectConsentRequest
                )
            } returns resultSuccessRejectConsent

            viewModel.rejectConsent(
                OpenFinanceConstants.REJECTED_USER_DETAIL,
                OpenFinanceConstants.REJECTED_USER
            )

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.rejectConsentLiveData.value is UIStateRejectConsent.Success)
        }

    @Test
    fun `it should set the error state in the error result of the consent refusal call`() =
        runTest {

            coEvery {
                useCaseRejectConsent(
                    OpenFinanceFactory.rejectConsentRequest
                )
            } returns OpenFinanceFactory.resultError

            viewModel.rejectConsent(
                OpenFinanceConstants.REJECTED_USER_DETAIL,
                OpenFinanceConstants.REJECTED_USER
            )

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.rejectConsentLiveData.value is UIStateRejectConsent.Error)
        }
}