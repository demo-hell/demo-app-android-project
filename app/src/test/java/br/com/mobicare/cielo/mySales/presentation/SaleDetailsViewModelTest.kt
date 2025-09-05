package br.com.mobicare.cielo.mySales.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.component.impersonate.data.model.request.ImpersonateRequest
import br.com.mobicare.cielo.component.impersonate.domain.usecase.PostImpersonateUseCase
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.mySales.MySalesFactory.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.mySales.MySalesFactory.estabelecimentoObj
import br.com.mobicare.cielo.mySales.MySalesFactory.estabelecimentoObj2
import br.com.mobicare.cielo.mySales.MySalesFactory.genericAPIError
import br.com.mobicare.cielo.mySales.MySalesFactory.genericSale
import br.com.mobicare.cielo.mySales.MySalesFactory.impersonateBO
import br.com.mobicare.cielo.mySales.MySalesFactory.impersonateSuccess
import br.com.mobicare.cielo.mySales.MySalesFactory.merchantSuccess
import br.com.mobicare.cielo.mySales.domain.usecase.GetSaleMerchantUseCase
import br.com.mobicare.cielo.mySales.presentation.viewmodel.SaleDetailsViewModel
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test



@OptIn(ExperimentalCoroutinesApi::class)
class SaleDetailsViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()


    private lateinit var viewModel: SaleDetailsViewModel
    private val merchantUseCase = mockk<GetSaleMerchantUseCase>()
    private val userPreferences = mockk<UserPreferences>()
    private val menuPreferences = mockk<MenuPreference>()
    private val ftUseCase = mockk<GetFeatureTogglePreferenceUseCase>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val impersonateUseCase = mockk<PostImpersonateUseCase>()



    @Before
    fun setup() {
        mockkObject(CieloApplication)
        every { CieloApplication.Companion.context } returns context
        viewModel = SaleDetailsViewModel(
            getSaleMerchantUseCase = merchantUseCase,
            impersonateUseCase = impersonateUseCase,
            userPreferences = userPreferences,
            menuPreferences = menuPreferences,
            featureToggleUseCase = ftUseCase
        )
    }


    @Test
    fun `it should return success for impersonate`() {

        //given
        every { userPreferences.token } returns ACCESS_TOKEN_MOCK
        coEvery { menuPreferences.getEstablishment() } returns estabelecimentoObj
        coEvery { impersonateUseCase.invoke(
            ec = EMPTY,
            type = EMPTY,
            impersonateRequest = ImpersonateRequest(fingerprint = EMPTY)
        ) } returns impersonateSuccess


        //when
        viewModel.createSaleDetailsStatement(
            sale = genericSale,
            fingerprint = EMPTY
        )

        //then
        dispatcherRule.advanceUntilIdle()
        assert(impersonateBO.accessToken != null)

    }


    @Test
    fun `it should return error when call impersonate`() = runBlocking {

        //given
        every { userPreferences.token } returns ACCESS_TOKEN_MOCK
        coEvery { menuPreferences.getEstablishment() } returns estabelecimentoObj
        coEvery { impersonateUseCase.invoke(any(),any(),any() )} returns genericAPIError


        //when
        viewModel.createSaleDetailsStatement(
            sale = genericSale,
            fingerprint = EMPTY
        )

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSaleMerchantViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }


    @Test
    fun `it should return success for get merchant`()  = runBlocking{

        //given
        every { userPreferences.token } returns ACCESS_TOKEN_MOCK
        coEvery { menuPreferences.getEstablishment() } returns estabelecimentoObj2
        coEvery { merchantUseCase.invoke(params = any()) } returns merchantSuccess

        //when
        viewModel.createSaleDetailsStatement(
            sale = genericSale,
            fingerprint = "TESTING"
        )

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSaleMerchantViewState.value.let {
            assert(it is MySalesViewState.SUCCESS)
        }
    }


    @Test
    fun `it should return error for get merchant`()  = runBlocking{

        //given
        every { userPreferences.token } returns ACCESS_TOKEN_MOCK
        coEvery { menuPreferences.getEstablishment() } returns estabelecimentoObj2
        coEvery { merchantUseCase.invoke(params = any()) } returns genericAPIError

        //when
        viewModel.createSaleDetailsStatement(
            sale = genericSale,
            fingerprint = "TESTING"
        )

        //then
        dispatcherRule.advanceUntilIdle()
        viewModel.getSaleMerchantViewState.value.let {
            assert(it is MySalesViewState.ERROR)
        }
    }
}