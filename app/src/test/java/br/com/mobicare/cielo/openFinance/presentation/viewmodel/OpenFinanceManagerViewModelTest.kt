package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.openFinance.domain.model.Brand
import br.com.mobicare.cielo.openFinance.domain.usecase.BrandsUseCase
import br.com.mobicare.cielo.openFinance.presentation.manager.OpenFinanceManagerViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFilterList
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.NONEXISTENT
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.TEST_ANOTHER_BANK
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.TEST_BANK
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OpenFinanceManagerViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseBrands = mockk<BrandsUseCase>()
    private val featureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>(relaxed = true)
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val successResponse = OpenFinanceFactory.successBrands
    private val resultSuccess = CieloDataResult.Success(successResponse)
    private lateinit var viewModel: OpenFinanceManagerViewModel
    private val context = mockk<Context>()

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        coEvery { featureTogglePreference(FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA) } returns
                CieloDataResult.Success(true)
        viewModel = OpenFinanceManagerViewModel(
            useCaseBrands,
            userPreferences,
            featureTogglePreference
        )
    }

    @Test
    fun `it should set the success state in the brands call success result`() {
        runTest {

            coEvery {
                useCaseBrands(EMPTY)
            } returns resultSuccess

            viewModel.getBanks(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getBanksLiveData.value is UIStateConsentDetail.Success)
        }
    }

    @Test
    fun `it should set the error state in the brands call error result`() {
        runTest {

            coEvery {
                useCaseBrands(EMPTY)
            } returns OpenFinanceFactory.resultError

            viewModel.getBanks(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getBanksLiveData.value is UIStateConsentDetail.Error)
        }
    }

    @Test
    fun `filterList returns full list when searchString is empty`() = runTest {
        coEvery { useCaseBrands(EMPTY) } returns resultSuccess
        viewModel.getBanks(EMPTY)

        viewModel.filterList(EMPTY)

        val value = viewModel.getListFilterLiveData.value
        assertTrue(value is UIStateFilterList.ListFiltered)
        assertEquals(successResponse, (value as UIStateFilterList.ListFiltered).data)
    }

    @Test
    fun `filterList returns filtered list when searchString matches bank brand`() = runTest {
        val bankList =
            listOf(Brand(TEST_BANK, listOf()), Brand(TEST_ANOTHER_BANK, listOf()))
        coEvery { useCaseBrands(EMPTY) } returns CieloDataResult.Success(bankList)
        viewModel.getBanks(EMPTY)

        viewModel.filterList(TEST_BANK)

        val value = viewModel.getListFilterLiveData.value
        assertTrue(value is UIStateFilterList.ListFiltered)
        assertEquals(listOf(bankList[ZERO]), (value as UIStateFilterList.ListFiltered).data)
    }

    @Test
    fun `filterList returns NotFound when searchString does not match any bank brand and list is not empty`() =
        runTest {
            val bankList =
                listOf(Brand(TEST_BANK, listOf()), Brand(TEST_ANOTHER_BANK, listOf()))
            coEvery { useCaseBrands(EMPTY) } returns CieloDataResult.Success(bankList)
            viewModel.getBanks(EMPTY)

            viewModel.filterList(NONEXISTENT)

            val value = viewModel.getListFilterLiveData.value
            assertTrue(value is UIStateFilterList.NotFound)
            (value as UIStateFilterList.NotFound).data?.let { assertTrue(it.isEmpty()) }
        }
}