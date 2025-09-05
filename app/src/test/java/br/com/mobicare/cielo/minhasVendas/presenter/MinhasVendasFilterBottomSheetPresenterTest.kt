package br.com.mobicare.cielo.minhasVendas.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetContract
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetPresenter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.SaleCardBrand
import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MinhasVendasFilterBottomSheetPresenterTest {

    private val filter = QuickFilter
            .Builder()
            .initialDate("2021-06-30")
            .finalDate("2021-06-30")
            .cardBrand(listOf(1))
            .paymentType(listOf(1))
            .grossAmount(478.0)
            .authorizationCode("059848")
            .nsu("59847")
            .build()

    lateinit var presenter: MinhasVendasFilterBottomSheetPresenter

    @Mock
    lateinit var view: MinhasVendasFilterBottomSheetContract.View

    @Mock
    lateinit var repository: MinhasVendasRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = MinhasVendasFilterBottomSheetPresenter(view, repository, userPreferences)
    }

    @Test
    fun `Error loading payment type and card brand when click in filter`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[4] as APICallbackDefault<ResultPaymentTypes, String>).onError(errorMessage)
        }.whenever(repository).getPaymentTypes(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                callback = any()
        )

        doAnswer {
            (it.arguments[2] as APICallbackDefault<ResultCardBrands, String>).onError(errorMessage)
        }.whenever(repository).getCardBrands(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                callback = any()
        )

        presenter.load(filter, true)

        verify(view).showError()
    }

    @Test
    fun `Loading payment type and card brand when click in filter in screen of view details sales`() {
        presenter.isMoreFilters = true

        val response = ResultPaymentTypes(cardBrands = listOf(SaleCardBrand("Visa", "1")),
                paymentTypes = listOf(PaymentType("1", "Crédito à vista")))

        doAnswer {
            (it.arguments[4] as APICallbackDefault<ResultPaymentTypes, String>).onSuccess(response)
        }.whenever(repository).getPaymentTypes(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                callback = any()
        )

        presenter.load(filter, true)

        val paymentType = response.paymentTypes?.map { ItemSelectable(it, true) }
        val listSaleCardBrand = response.cardBrands?.map {
            CardBrand(it.code?.toInt()!!, it.name!!)
        }
        val cardBrand = listSaleCardBrand?.map { ItemSelectable(it, true) }

        paymentType?.let { verify(view).showPaymentTypes(it) }
        cardBrand?.let { verify(view).showCardBrands(it) }
        verify(view).loadMoreFilters(filter)
        verify(view).showCancelInputs()
    }

    @Test
    fun `Loading payment type and card brand when click in filter in canceled sales`() {
        presenter.isCanceledFilters = true

        val response = ResultPaymentTypes(cardBrands = listOf(SaleCardBrand("Visa", "1")),
                paymentTypes = listOf(PaymentType("1", "Crédito à vista")))

        doAnswer {
            (it.arguments[3] as APICallbackDefault<ResultPaymentTypes, String>).onSuccess(response)
        }.whenever(repository).filterCanceledSells(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                callback = any()
        )

        presenter.load(filter, true)
        val paymentType = response.paymentTypes?.map { ItemSelectable(it, true) }
        val listSaleCardBrand = response.cardBrands?.map {
            CardBrand(it.code?.toInt()!!, it.name!!)
        }
        val cardBrand = listSaleCardBrand?.map { ItemSelectable(it, true) }

        paymentType?.let { verify(view).showPaymentTypes(it) }
        cardBrand?.let { verify(view).showCardBrands(it) }
        verify(view).loadMoreFilters(filter)
        verify(view).showCancelInputs()
    }


    @Test
    fun `Error card brand when click in filter in canceled sales`() {
        presenter.isCanceledFilters = true

        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[2] as APICallbackDefault<ResultCardBrands, String>).onError(errorMessage)
        }.whenever(repository).getCardBrands(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                callback = any()
        )

        presenter.loadBrands()

        verify(view).loadMoreFilters(null)
        verify(view).showCancelInputs()

    }

}