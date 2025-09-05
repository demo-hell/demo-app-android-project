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
import br.com.mobicare.cielo.mySales.MySalesFactory.genericCanceledAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.summarySalesAPISuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.userObj
import br.com.mobicare.cielo.mySales.domain.usecase.GetCanceledSalesUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetGA4UseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesViewModel
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
class HomeSalesViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()


    private lateinit var viewModel: HomeSalesViewModel
    private val saleUseCase = mockk<GetSalesUseCase>()
    private val canceledSaleUseCase = mockk<GetCanceledSalesUseCase>()
    private val userPreferences = mockk<UserPreferences>()
    private val userObjUseCase = mockk<GetUserObjUseCase>()
    private val token = MySalesFactory.ACCESS_TOKEN_MOCK
    private val context = mockk<Context>(relaxed = true)
    private val getGA4UseCase = mockk<GetGA4UseCase>(relaxed = true)

    private val quickFilter = MySalesFactory.quickFilter
    private val canceledSaleQuickFilter = MySalesFactory.canceledSaleQuickFilter


    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = HomeSalesViewModel(
            getSalesUseCase = saleUseCase,
            getCanceledSalesUseCase = canceledSaleUseCase,
            userObjUseCase = userObjUseCase,
            userPreferences = userPreferences,
            getGA4UseCase = getGA4UseCase
        )
    }


    //region - Non canceled sales tests

    @Test
    fun `it should return success when call non canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery {  saleUseCase.invoke(params = any()) } returns summarySalesAPISuccess

        //when
        viewModel.getSales(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesDataViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return error when call non canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { userObjUseCase.invoke() } returns CieloDataResult.Success(userObj)
        coEvery {  saleUseCase.invoke(params = any()) } returns genericAPIError

        //when
        viewModel.getSales(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesDataViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }

    @Test
    fun `it should return empty state when call non canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery {  saleUseCase.invoke(params = any()) } returns emptyResult

        //when
        viewModel.getSales(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesDataViewState.value.let {
            assert(it is MySalesViewState.EMPTY)
        }
    }


    @Test
    fun `it should return fullscreen error when call for non canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns false
        every { userPreferences.token } returns token
        coEvery { saleUseCase.invoke(params = any()) }


        //when
        viewModel.getSales(quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSalesDataViewState.value.let {
            assert(it is MySalesViewState.ERROR_FULL_SCREEN)
        }
    }

    //endregion


    //region - canceled sales

    @Test
    fun `it should return success when call canceled sales`() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { canceledSaleUseCase.invoke(params = any()) } returns genericCanceledAPISuccess

        //when
        viewModel.getSales(canceledSaleQuickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getCanceledSalesDataViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return error when call canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { userObjUseCase.invoke() } returns CieloDataResult.Success(userObj)
        coEvery {  canceledSaleUseCase.invoke(params = any()) } returns genericAPIError

        //when
        viewModel.getSales(canceledSaleQuickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getCanceledSalesDataViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }



    @Test
    fun `it should return empty state when call canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery {  canceledSaleUseCase.invoke(params = any()) } returns emptyResult

        //when
        viewModel.getSales(canceledSaleQuickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getCanceledSalesDataViewState.value.let {
            assert(it is MySalesViewState.EMPTY)
        }
    }


    @Test
    fun `it should return fullscreen error when call for canceled sales`() {

        //given
        every { userPreferences.isConvivenciaUser } returns false
        every { userPreferences.token } returns token
        coEvery { canceledSaleUseCase.invoke(params = any()) }


        //when
        viewModel.getSales(canceledSaleQuickFilter)

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getCanceledSalesDataViewState.value.let {
            assert(it is MySalesViewState.ERROR_FULL_SCREEN)
        }
    }

    //endregion


}