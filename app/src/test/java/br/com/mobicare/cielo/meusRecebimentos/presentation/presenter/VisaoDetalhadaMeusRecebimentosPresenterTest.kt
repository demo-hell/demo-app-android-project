package br.com.mobicare.cielo.meusRecebimentos.presentation.presenter

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.meusrecebimentosnew.models.Link
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.DetailSummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Pagination
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.ReceivableSummary
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Bank
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosContract
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val PAGE_NUM: Long = 1
const val PAGE_SIZE: Long = 25
const val TOTAL: Long = 1
const val NUMBER_PAGE: Long = 1

class VisaoDetalhadaMeusRecebimentosPresenterTest {

    private val url = "ae198e8"

    private val links = listOf(Link("e7df20b", "ae198e8"))

    private val items = Item(merchantId = "2015257327",
            paymentNode = 6188521,
            paymentDate = "2021-08-09",
            saleDate = null,
            paymentScheduleDate = null,
            operationNumber = null,
            roNumber = null,
            status = null,
            cardBrand = "Agiplan",
            cardBrandCode = 11,
            paymentType = null,
            paymentTypeCode = null,
            productType = null,
            productTypeCode = null,
            transactionTypeCode = 2,
            transactionType = "Venda crédito",
            terminal = null,
            description = null,
            netAmount = 20147.51,
            grossAmount = 21064.0,
            mdrFeeAmount = null,
            anticipationFee = null,
            anticipationDiscountAmount = null,
            quantity = 86,
            adjustmentDescription = null,
            initialDate = null,
            finalDate = null,
            links = links,
            code = null,
            type = null,
            name = null,
            agency = null,
            account = null,
            accountDigit = null,
            bank = null)

    private val receivable = Receivable(merchantId = "2015257327", paymentNode = null, data = null, paymentDate = "2021-08-09",
            saleDate = "2021-07-10", truncatedCardNumber = "0014", transactionId = null, authorizationCode = "083264", authorizationDate = "2021-07-10",
            operationDate = "2021-07-12", operationNumber = "0", roNumber = "210710", type = null, nsu = "83263", status = "Enviado Banco", statusCode = 2,
            cardBrand = "Agiplan", cardBrandCode = 11, netAmount = 147.3, grossAmount = 154.0, paymentType = "Crédito à vista", paymentTypeCode = 1, productTypeCode = 31,
            productType = "Crédito à vista", transactionTypeCode = 2, transactionType = "Venda crédito", installments = 0, installment = 0, terminal = "21257327", saleCode = "21193511000320100000000001000",
            anticipationCode = null, initialPaymentDate = null, anticipationDays = null, quantity = null, channelCode = 1, channel = "Máquina", orderNumber = null, mdrFee = 4.35, mdrFeeAmount = -6.7, cieloPromo = false,
            promoAmount = 0.0, getFast = false, rejectedSale = false, availableDate = "2021-07-12", shipmentFee = 0.0, withdrawAmount = 0.0, downPaymentAmount = 0.0, description = null, invoiceNumber = "000000000", paymentDescription = "Crédito à vista",
            date = null, pendingAmount = null, chargedAmount = null, totalDebtAmount = null, anticipationFee = 0, entryModeCode = 0, entryMode = "Reentrada manual", bank = Bank(account = null, agencyDigit = null, name = "Banco Do Brasil S.A.", accountDigit = "1",
            accountNumber = "1111", agency = "7964", brands = null, code = "1", digitalAccount = false, imgSource = "", savingsAccount = false), initialDate = null, finalDate = null, installmentDescription = "10x", transactionPixId = "00001",
            typeAccountPix = 1,dateTransferAccountPix = "2023-08-08", codeFarol = 3,  descriptionFarol = "Rejeitado (farol vermerlho)")

    private val response = DetailSummaryViewResponse(summary = ReceivableSummary(totalQuantity = 1, totalAmount = 21064.0, totalNetAmount = 20147.51, totalAverageAmount = null),
            pagination = Pagination(pageNumber = PAGE_NUM, pageSize = PAGE_SIZE, totalElements = TOTAL, firstPage = true, lastPage = false, numPages = NUMBER_PAGE), items = listOf(receivable))

    private val responseEmpty = DetailSummaryViewResponse(summary = ReceivableSummary(totalQuantity = 1, totalAmount = 21064.0, totalNetAmount = 20147.51, totalAverageAmount = null),
            pagination = Pagination(pageNumber = PAGE_NUM, pageSize = PAGE_SIZE, totalElements = TOTAL, firstPage = true, lastPage = false, numPages = NUMBER_PAGE), items = listOf())

    @Mock
    lateinit var view: VisaoDetalhadaMeusRecebimentosContract.View

    @Mock
    lateinit var repository: MeusRecebimentosInteractor

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: VisaoDetalhadaMeusRecebimentosPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = VisaoDetalhadaMeusRecebimentosPresenter(view, repository, uiScheduler, ioScheduler)
    }

    @Test
    fun `When calling loadWithCustomFilter() the return is success`() {
        val quickFilter = QuickFilter.Builder()
                .initialDate("2021-08-09")
                .finalDate("2021-08-09")
                .cardBrand(listOf(11))
                .transactionTypeCode(2)
                .build()

        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(repository).getDetailSummaryView(url = url, initialDate = quickFilter.initialDate, finalDate = quickFilter.finalDate, cardBrandCode = quickFilter.cardBrand, transactionTypeCode = quickFilter.transactionTypeCode)

        presenter.loadWithCustomFilter(quickFilter, 1, false, items, 2)
        verify(view).showLoading()
        verify(view).hideRefreshLoading()
        verify(view).hideLoading()
        verify(view).showBottom(any(), any())
        verify(view).showHeader(any(), any())
        verify(view).showItens(any(), any())

        assertEquals(response.items, listOf(receivable))

    }

    @Test
    fun `When calling loadWithCustomFilter() the return is success but the array Items is empty`() {
        val quickFilter = QuickFilter.Builder()
                .initialDate("2021-08-09")
                .finalDate("2021-08-09")
                .cardBrand(listOf(11))
                .transactionTypeCode(2)
                .build()

        val returnSuccess = Observable.just(responseEmpty)
        doReturn(returnSuccess).whenever(repository).getDetailSummaryView(url = url, initialDate = quickFilter.initialDate, finalDate = quickFilter.finalDate, cardBrandCode = quickFilter.cardBrand, transactionTypeCode = quickFilter.transactionTypeCode)

        presenter.loadWithCustomFilter(quickFilter, 1, false, items, 2)
        verify(view).showLoading()
        verify(view).hideRefreshLoading()
        verify(view).hideLoading()
        verify(view).showBottom(any(), any())
        verify(view).showErrorEmptyResult()

        assertEquals(responseEmpty.items, listOf<Receivable>())
    }

    @Test
    fun `When calling loadWithCustomFilter() the return is one error 500`() {
        val quickFilter = QuickFilter.Builder()
                .initialDate("2021-08-09")
                .finalDate("2021-08-09")
                .cardBrand(listOf(11))
                .transactionTypeCode(2)
                .build()

        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(repository).getDetailSummaryView(url = url, initialDate = quickFilter.initialDate, finalDate = quickFilter.finalDate, cardBrandCode = quickFilter.cardBrand, transactionTypeCode = quickFilter.transactionTypeCode)

        presenter.loadWithCustomFilter(quickFilter, 1, false, items, 2)
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(any())

        assertEquals(exception.httpStatus, 500)

    }
}