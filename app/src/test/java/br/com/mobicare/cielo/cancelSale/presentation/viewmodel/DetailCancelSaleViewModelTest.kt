package br.com.mobicare.cielo.cancelSale.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.cancelSale.domain.usecase.BalanceInquiryUseCase
import br.com.mobicare.cielo.cancelSale.domain.usecase.CancelSaleUseCase
import br.com.mobicare.cielo.cancelSale.presentation.detail.DetailCancelSaleViewModel
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateCancelSale
import br.com.mobicare.cielo.cancelSale.presentation.utils.UIStateBalanceInquiry
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.AUTHORIZATION_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.AUTHORIZATION_DATE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.CARD_BRAND_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.GROSS_AMOUNT
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.NSU
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.PAYMENT_TYPE_CODE
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.SALE_MERCHANT
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleConstants.TRUNCATED_CARD_NUMBER
import br.com.mobicare.cielo.cancelSale.utils.CancelSaleFactory
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class DetailCancelSaleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseBalanceInquiry = mockk<BalanceInquiryUseCase>()
    private val useCaseCancelSale = mockk<CancelSaleUseCase>()
    private val responseBalanceInquiry = CancelSaleFactory.responseBalanceInquiry
    private val cancelSaleResponse = CancelSaleFactory.cancelSaleResponse
    private val resultSuccessBalanceInquiry = CieloDataResult.Success(responseBalanceInquiry)
    private val resultSuccessCancelSale = CieloDataResult.Success(cancelSaleResponse)
    private lateinit var viewModel: DetailCancelSaleViewModel

    @Before
    fun setUp() {
        viewModel = DetailCancelSaleViewModel(useCaseBalanceInquiry, useCaseCancelSale)
    }

    @Test
    fun `it should set the success state when balance inquiry is successful`() = runTest {
        coEvery { useCaseBalanceInquiry.invoke(any()) } returns resultSuccessBalanceInquiry

        viewModel.getBalanceInquiry(
            CARD_BRAND_CODE,
            AUTHORIZATION_CODE,
            NSU,
            TRUNCATED_CARD_NUMBER,
            AUTHORIZATION_DATE,
            PAYMENT_TYPE_CODE,
            GROSS_AMOUNT,
            SALE_MERCHANT
        )

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.getBalanceInquiryLiveData.value is UIStateBalanceInquiry.Success)
    }

    @Test
    fun `it should set the error state when balance inquiry fails`() = runTest {
        coEvery { useCaseBalanceInquiry.invoke(any()) } returns CancelSaleFactory.resultError

        viewModel.getBalanceInquiry(
            CARD_BRAND_CODE,
            AUTHORIZATION_CODE,
            NSU,
            TRUNCATED_CARD_NUMBER,
            AUTHORIZATION_DATE,
            PAYMENT_TYPE_CODE,
            GROSS_AMOUNT,
            SALE_MERCHANT
        )

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.getBalanceInquiryLiveData.value is UIStateBalanceInquiry.Error)
    }

    @Test
    fun `it should set the success state when cancel sale is successful`() = runTest {
        coEvery { useCaseCancelSale.invoke(any(), any()) } returns resultSuccessCancelSale

        viewModel.cancelSale(100.0, "2024-08-07", "123456")

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.cancelSaleLiveData.value is UIStateCancelSale.Success)
    }

    @Test
    fun `it should set the error state when cancel sale fails with generic error`() = runTest {
        coEvery { useCaseCancelSale.invoke(any(), any()) } returns CancelSaleFactory.resultError

        viewModel.cancelSale(100.0, "2024-08-07", "123456")

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.cancelSaleLiveData.value is UIStateCancelSale.ErrorGeneric)
    }
}