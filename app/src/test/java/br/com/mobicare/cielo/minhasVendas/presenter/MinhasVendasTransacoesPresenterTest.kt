package br.com.mobicare.cielo.minhasVendas.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.minhasVendas.domain.ResultSummarySales
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MinhasVendasTransacoesContract
import br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MinhasVendasTransacoesPresenter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val AUTHENTICATION = "Basic null"

class MinhasVendasTransacoesPresenterTest {

    private val listSales: List<Sale> = listOf(
        Sale(id = "21181126000127700000000003000",
            date = "2021 - 06 - 30 T15 :01:57",
            cardBrand = "Visa",
            cardBrandDescription = null,
            paymentTypeCode = "1",
            paymentType = "Crédito à vista",
            amount = null,
            truncatedCardNumber = "7972",
            terminal = "21257327",
            authorizationCode = "059848",
            authorizationDate = "2021 - 06 - 30",
            status = "Aprovada",
            channel = "Máquina",
            statusCode = null,
            nsu = "59847",
            tid = null,
            paymentSolutionType = null,
            paymentSolutionCode = null,
            paymentScheduleDate = "2021 - 07 - 30",
            paymentDate = null,
            grossAmount = 478.0,
            netAmount = 468.44,
            administrationFee = 2.0,
            cardBrandCode = 1,
            saleGrossAmount = null,
            saleDate = null,
            merchantId = "2015257327",
            mdrFee = null,
            mdrFeeAmount = -9.56,
            cardNumber = null,
            installments = 10,
            transactionId = null,
            paymentNode = 6188521,
            transactionPixId = "000001"

    )
    )

    private val summary = Summary(totalQuantity = 10,
            totalAmount = 5802.0,
            totalNetAmount = 5685.96)

    private val pagination = Pagination(pageNumber = 1,
            pageSize = 25,
            totalElements = 10,
            firstPage = true,
            lastPage = true,
            numPages = 1)

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


    lateinit var presenter: MinhasVendasTransacoesPresenter

    @Mock
    lateinit var view: MinhasVendasTransacoesContract.View

    @Mock
    lateinit var minhasVendasRepository: MinhasVendasRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = MinhasVendasTransacoesPresenter(view, minhasVendasRepository, userPreferences)
    }

    @Test
    fun `success return for summary sales when refresh`() {
        val response = ResultSummarySales(summary = summary, pagination = pagination, items = listSales)
        val sales = ArrayList<Sale>()

        sales.addAll(response.items)

        doAnswer {
            (it.arguments[18] as APICallbackDefault<ResultSummarySales, String>).onSuccess(response)
        }.whenever(minhasVendasRepository).getSummarySales(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                initialAmount = eq(filter.initialAmount),
                finalAmount = eq(filter.finalAmount),
                customId = eq(filter.customId),
                saleCode = eq(filter.saleCode),
                truncatedCardNumber = eq(filter.truncatedCardNumber),
                cardBrands = eq(filter.cardBrand),
                paymentTypes = eq(filter.paymentType),
                terminal = eq(filter.terminal),
                status = eq(filter.status),
                cardNumber = eq(filter.cardNumber),
                nsu = eq(filter.nsu),
                authorizationCode = eq(filter.authorizationCode),
                page = eq(1),
                pageSize = eq(25),
                callback = any()
        )

        presenter.refresh(filter)

        verify(view).showSummary(response.summary)
        verify(view).showSales(sales)
    }

    @Test
    fun `success return for summary sales when refresh but list sales is empty`() {
        val response = ResultSummarySales(summary = summary, pagination = pagination, items = listOf())

        doAnswer {
            (it.arguments[18] as APICallbackDefault<ResultSummarySales, String>).onSuccess(response)
        }.whenever(minhasVendasRepository).getSummarySales(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq(AUTHENTICATION),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                initialAmount = eq(filter.initialAmount),
                finalAmount = eq(filter.finalAmount),
                customId = eq(filter.customId),
                saleCode = eq(filter.saleCode),
                truncatedCardNumber = eq(filter.truncatedCardNumber),
                cardBrands = eq(filter.cardBrand),
                paymentTypes = eq(filter.paymentType),
                terminal = eq(filter.terminal),
                status = eq(filter.status),
                cardNumber = eq(filter.cardNumber),
                nsu = eq(filter.nsu),
                authorizationCode = eq(filter.authorizationCode),
                page = eq(1),
                pageSize = eq(25),
                callback = any()
        )

        presenter.refresh(filter)

        verify(view).showEmptyResult()
    }

    @Test
    fun `error return for summary sales when refresh`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorMessage = APIUtils.convertToErro(exception)

        doAnswer {
            (it.arguments[18] as APICallbackDefault<ResultSummarySales, String>).onError(errorMessage)
        }.whenever(minhasVendasRepository).getSummarySales(
                accessToken = eq(ACCESS_TOKEN_MOCK),
                authorization = eq( AUTHENTICATION),
                initialDate = eq(filter.initialDate!!),
                finalDate = eq(filter.finalDate!!),
                initialAmount = eq(filter.initialAmount),
                finalAmount = eq(filter.finalAmount),
                customId = eq(filter.customId),
                saleCode = eq(filter.saleCode),
                truncatedCardNumber = eq(filter.truncatedCardNumber),
                cardBrands = eq(filter.cardBrand),
                paymentTypes = eq(filter.paymentType),
                terminal = eq(filter.terminal),
                status = eq(filter.status),
                cardNumber = eq(filter.cardNumber),
                nsu = eq(filter.nsu),
                authorizationCode = eq(filter.authorizationCode),
                page = eq(1),
                pageSize = eq(25),
                callback = any()
        )

        presenter.refresh(filter)

        verify(view).showError(errorMessage)
    }
}
