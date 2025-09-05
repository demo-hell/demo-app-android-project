package br.com.mobicare.cielo.mySales.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.MySalesFactory.emptyResult
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.summarySalesAPISuccess
import br.com.mobicare.cielo.mySales.domain.usecase.GetMySalesTransactionsUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesTransactionsViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class MySalesTransactionsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()


    private lateinit var viewModel: MySalesTransactionsViewModel
    private val transactionUseCase = mockk<GetMySalesTransactionsUseCase>()
    private val ftUseCase = mockk<GetFeatureTogglePreferenceUseCase>(relaxed = true)
    private val userPreferences = mockk<UserPreferences>()
    private val userObjUseCase = mockk<GetUserObjUseCase>()
    private val token = MySalesFactory.ACCESS_TOKEN_MOCK

    private val quickFilter = MySalesFactory.quickFilter
    private val context = mockk<Context>(relaxed = true)


    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = MySalesTransactionsViewModel(
            getMySalesTransactionsUseCase = transactionUseCase,
            userPreferences = userPreferences,
            userObjUseCase = userObjUseCase,
            featureToggleUseCase = ftUseCase
        )
    }


    @Test
    fun `it should return success when call getMySalesTransactions`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { transactionUseCase.invoke(params = any()) } returns summarySalesAPISuccess

        //when
        viewModel.getMySalesTransactions(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesTransactionViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return error when call getMySalesTransactions`() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { userObjUseCase.invoke() } returns CieloDataResult.Success(MySalesFactory.userObj)
        coEvery { transactionUseCase.invoke(params = any()) } returns genericAPIError

        //when
        viewModel.getMySalesTransactions(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesTransactionViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }

    }

    @Test
    fun `it should return empty state when call getMySalesTransactions`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { transactionUseCase.invoke(params = any()) } returns emptyResult

        //when
        viewModel.getMySalesTransactions(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesTransactionViewState.value.let {
            assert(it is MySalesViewState.EMPTY)
        }
    }


    @Test
    fun `check if filter is selected`() {

        val isSelected = viewModel.isFilterNotSelected(quickFilter)
        assertEquals(isSelected,true)
    }


}