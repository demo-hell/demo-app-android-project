package br.com.mobicare.cielo.mySales.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.mySales.MySalesFactory
import br.com.mobicare.cielo.mySales.MySalesFactory.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.mySales.domain.usecase.GetHomeCardSummarySalesUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.HomeSalesCardViewModel
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
class HomeSalesCardViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val genericHomeCardSummarySaleBO = MySalesFactory.genericHomeCardSummarySaleBO
    private val apiResultError = MySalesFactory.genericAPIError
    private val resultEmpty = MySalesFactory.emptyResult
    private val token = ACCESS_TOKEN_MOCK
    private val userObj = MySalesFactory.userObj

    private val useCase = mockk<GetHomeCardSummarySalesUseCase>()
    private val userUseCase = mockk<GetUserObjUseCase>()
    private val userPreferences = mockk<UserPreferences>()
    private val featureTogglePreference = mockk<FeatureTogglePreference>()
    private lateinit var viewModel: HomeSalesCardViewModel
    private val context = mockk<Context>(relaxed = true)

    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = HomeSalesCardViewModel(useCase, userPreferences, userUseCase, featureTogglePreference)
    }

    @Test
    fun `it should return success result `() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { useCase(params = any()) } returns CieloDataResult.Success(genericHomeCardSummarySaleBO)

        //when
        viewModel.getHomeCardSummarySale(MySalesFactory.quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()

        viewModel.getHomeCardSummarySalesViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return API error result `() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { userUseCase.invoke() } returns CieloDataResult.Success(userObj)

        coEvery { useCase(params = any()) } returns apiResultError

        //when
        viewModel.getHomeCardSummarySale(MySalesFactory.quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()

        viewModel.getHomeCardSummarySalesViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }



    @Test
    fun `it should return empty state result`() {
        //given
        every { userPreferences.isConvivenciaUser } returns true
        every { userPreferences.token } returns token
        coEvery { useCase(params = any()) } returns resultEmpty

        //when
        viewModel.getHomeCardSummarySale(MySalesFactory.quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()

        viewModel.getHomeCardSummarySalesViewState.value.let {
            assert(it is MySalesViewState.EMPTY)
        }
    }



    @Test
    fun `it should return full screen error result`() {
        //given
        every { userPreferences.isConvivenciaUser } returns false
        every { userPreferences.token } returns token
        coEvery { useCase(params = any()) }

        //when
        viewModel.getHomeCardSummarySale(MySalesFactory.quickFilter)

        //then
        dispatcherRule.advanceUntilIdle()

        viewModel.getHomeCardSummarySalesViewState.value.let {
            assert(it is MySalesViewState.ERROR_FULL_SCREEN)
        }
    }

}