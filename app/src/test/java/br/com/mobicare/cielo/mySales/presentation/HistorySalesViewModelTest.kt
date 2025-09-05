package br.com.mobicare.cielo.mySales.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.MySalesFactory.emptyResult
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.quickFilter
import br.com.mobicare.cielo.mySales.MySalesFactory.saleHistoryAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.userObj
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesHistoryUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HistorySalesViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class HistorySalesViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()


    private lateinit var viewModel: HistorySalesViewModel
    private val useCase = mockk<GetSalesHistoryUseCase>()
    private val userPreferences = mockk<UserPreferences>()
    private val userObjUseCase = mockk<GetUserObjUseCase>()
    private val token = MySalesFactory.ACCESS_TOKEN_MOCK
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = HistorySalesViewModel(useCase,userPreferences,userObjUseCase)
    }


    @Test
    fun `it should return success for getSalesHistoryData`() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { useCase(params = any()) } returns saleHistoryAPISuccess

        //when
        viewModel.getSalesHistory(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesHistoryDataViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return error for getSalesHistoryData`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { userObjUseCase.invoke() } returns CieloDataResult.Success(userObj)
        coEvery { useCase.invoke(params = any()) } returns genericAPIError

        //when
        viewModel.getSalesHistory(quickFilter)


        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesHistoryDataViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }


    @Test
    fun `it should return empty for getSalesHistoryData`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { useCase.invoke(params = any()) } returns emptyResult

        //when
        viewModel.getSalesHistory(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesHistoryDataViewState.value.let {
            assert(it is MySalesViewState.EMPTY)
        }

    }





}