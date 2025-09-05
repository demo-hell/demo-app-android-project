package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.userPreferences.DeleteUserPreferencesUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.openFinance.domain.usecase.GetPixMerchantListOpenFinanceUseCase
import br.com.mobicare.cielo.openFinance.presentation.home.OpenFinanceHomeViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateMerchantList
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PixMerchantViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseGetPixMerchantList = mockk<GetPixMerchantListOpenFinanceUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val useCaseDeleteUserPreferences = mockk<DeleteUserPreferencesUseCase>(relaxed = true)
    private val successResponse = OpenFinanceFactory.successResponsePixMerchant
    private val resultSuccess = CieloDataResult.Success(successResponse)
    private lateinit var viewModel: OpenFinanceHomeViewModel

    @Before
    fun setUp() {
        viewModel = OpenFinanceHomeViewModel(
            useCaseGetPixMerchantList
        )
    }

    @Test
    fun `it should set success state on success result of get pix merchant account list call`() =
        runTest {

            coEvery { useCaseGetPixMerchantList() } returns resultSuccess

            viewModel.getPixMerchantAccountList()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getPixMerchantAccountListLiveData.value is UIStateMerchantList.Success)
        }

    @Test
    fun `it should set error state on error result of get pix merchant account list call`() =
        runTest {

            coEvery { useCaseGetPixMerchantList() } returns OpenFinanceFactory.resultError

            viewModel.getPixMerchantAccountList()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getPixMerchantAccountListLiveData.value is UIStateMerchantList.Error)
        }

    @Test
    fun `it should set not found state on not found result of get pix merchant account list call`() =
        runTest {

            coEvery { useCaseGetPixMerchantList() } returns OpenFinanceFactory.resultErrorNotFound

            viewModel.getPixMerchantAccountList()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getPixMerchantAccountListLiveData.value is UIStateMerchantList.NotFound)
        }
}

