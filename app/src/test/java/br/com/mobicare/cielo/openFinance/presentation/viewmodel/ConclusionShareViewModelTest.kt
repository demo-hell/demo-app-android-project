package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.openFinance.domain.usecase.ChangeOrRenewShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.ConfirmShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.GivenUpShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.conclusion.OpenFinanceConclusionViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConclusionShare
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.authorizationCodeSample
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ConclusionShareViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseConfirmShare = mockk<ConfirmShareUseCase>()
    private val useCaseGivenUpShare = mockk<GivenUpShareUseCase>()
    private val useCaseChangeOrRenewShare = mockk<ChangeOrRenewShareUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val requestConfirm = OpenFinanceFactory.requestConfirmShare
    private val requestGivenUp = OpenFinanceFactory.requestGivenUpShare
    private val successResponseConfirmShare = OpenFinanceFactory.responseConfirmShare
    private val successResponseGivenUpShare = OpenFinanceFactory.responseGivenUpShare
    private val resultSuccessConfirm = CieloDataResult.Success(successResponseConfirmShare)
    private val resultSuccessGivenUp = CieloDataResult.Success(successResponseGivenUpShare)
    private lateinit var viewModel: OpenFinanceConclusionViewModel

    @Before
    fun setUp() {
        viewModel = OpenFinanceConclusionViewModel(useCaseConfirmShare, useCaseGivenUpShare, useCaseChangeOrRenewShare, userPreferences)
    }

    @Test
    fun `it should set the success state in the confirm share call success result`() =
        runTest {

            every { userPreferences.authorizationCodeOPF } returns authorizationCodeSample

            coEvery { useCaseConfirmShare(requestConfirm) } returns resultSuccessConfirm

            viewModel.confirmOrGivenUpShare()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.conclusionShareLiveData.value is UIStateConclusionShare.SuccessShare)
        }

    @Test
    fun `it should set the error state in the confirm share call error result`() =
        runTest {

            every { userPreferences.authorizationCodeOPF } returns authorizationCodeSample

            coEvery { useCaseConfirmShare(requestConfirm) } returns OpenFinanceFactory.resultError

            viewModel.confirmOrGivenUpShare()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.conclusionShareLiveData.value is UIStateConclusionShare.ErrorShare)
        }

    @Test
    fun `it should set the success state in the given up share call success result`() =
        runTest {

            coEvery { useCaseGivenUpShare(requestGivenUp) } returns resultSuccessGivenUp

            viewModel.confirmOrGivenUpShare()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.conclusionShareLiveData.value is UIStateConclusionShare.ErrorShare)
        }

    @Test
    fun `it should set the error state in the given up share call error result`() =
        runTest {

            coEvery { useCaseGivenUpShare(requestGivenUp) } returns OpenFinanceFactory.resultError

            viewModel.confirmOrGivenUpShare()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.conclusionShareLiveData.value is UIStateConclusionShare.ErrorShare)
        }
}