package br.com.mobicare.cielo.mySales.presentation



import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.MySalesFactory.cardBrandsFilterSuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.paymentTypeFilterSuccess
import br.com.mobicare.cielo.mySales.domain.usecase.GetCardBrandsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetFilteredCanceledSellsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetPaymentTypeUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.MySalesFiltersViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations


@OptIn(ExperimentalCoroutinesApi::class)
class MySalesFiltersViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()


    private lateinit var viewModel: MySalesFiltersViewModel
    private val paymentTypeFilterUseCase = mockk<GetPaymentTypeUseCase>()
    private val canceledSellsUseCase = mockk<GetFilteredCanceledSellsUseCase>()
    private val cardBrandFilterUseCase = mockk<GetCardBrandsUseCase>()
    private val token = MySalesFactory.ACCESS_TOKEN_MOCK
    private val userPreferences = mockk<UserPreferences>()
    private val quickFilter = MySalesFactory.quickFilter
    private val context = mockk<Context>(relaxed = true)


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = MySalesFiltersViewModel(
            paymentTypeFilterUseCase = paymentTypeFilterUseCase,
            cardBrandFilterUseCase = cardBrandFilterUseCase,
            canceledSalesFilter = canceledSellsUseCase,
            userPreferences = userPreferences
        )


    }


    @Test
    fun `it should get card brands`() = runBlocking {

        //given
        every { userPreferences.token } returns token
        coEvery { cardBrandFilterUseCase.invoke(params = any()) } returns cardBrandsFilterSuccess

        //when
        viewModel.getFilters(quickFilter,false)


        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.brandsLiveData.value.let {
            assert(it?.isNotEmpty() == true)
        }
    }


    @Test
    fun `it should get payment types`() = runBlocking {

        //given
        viewModel.isCanceledFilters = false
        every { userPreferences.token } returns token
        coEvery { cardBrandFilterUseCase.invoke(params = any()) } returns cardBrandsFilterSuccess
        coEvery { paymentTypeFilterUseCase.invoke(params = any()) } returns paymentTypeFilterSuccess

        //when
        viewModel.getFilters(quickFilter,true)


        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.paymentTypeLiveData.value.let {
            assert(it?.isNotEmpty() == true)
        }
    }


    @Test
    fun `it should return error when get payment types and card brands`() = runBlocking {

        //given
        val cardBrandsField = MySalesFiltersViewModel::class.java.getDeclaredField("brands")
        cardBrandsField.isAccessible = true
        val cardBrandValue = cardBrandsField.get(viewModel)


        val paymentTypesField = MySalesFiltersViewModel::class.java.getDeclaredField("paymentTypes")
        paymentTypesField.isAccessible = true
        val paymentTypesValue = paymentTypesField.get(viewModel)

        every { userPreferences.token } returns token
        coEvery { paymentTypeFilterUseCase.invoke(params = any()) } returns genericAPIError
        coEvery { cardBrandFilterUseCase.invoke(params = any()) } returns genericAPIError


        //when
        viewModel.getFilters(quickFilter,true)

        //then
        dispatcherRule.advanceUntilIdle()
        assert(cardBrandValue == null)
        assert(paymentTypesValue == null)
        assert(!viewModel.isCanceledFilters)
        assert(!viewModel.isMoreFilters)

    }

    @Test
    fun `it should return success for canceled sells from service`() = runBlocking {

        //given
        viewModel.isCanceledFilters = true
        viewModel.isMoreFilters = true
        every { userPreferences.token } returns token
        coEvery { canceledSellsUseCase.invoke(params = any()) } returns paymentTypeFilterSuccess


        //when
        viewModel.getFilters(quickFilter,true)


        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.paymentTypeLiveData.value.let {
            assert(it?.isNotEmpty() == true)
        }


    }


}