package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.extrato.data.managers.StatementRepository
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.domains.entities.CreditCardStatement
import br.com.mobicare.cielo.meusCartoes.domains.entities.Statement
import br.com.mobicare.cielo.meusCartoes.presentation.ui.LastTransactionsContract
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val EC_MOCK = "12345"

class LastTransactionsPresenterTest {

    private val statements = listOf(
            Statement(dateHourTransaction = "2021-08-27T11:25:27.027Z",
                    establishment = "Cielo",
                    operationType = "DEBIT",
                    amount = 23.5
            ),
            Statement(dateHourTransaction = "2021-08-27T11:25:27.027Z",
                    establishment = "Cielo",
                    operationType = "CREDIT",
                    amount = 42.5
            ),
            Statement(dateHourTransaction = "2021-08-27T11:25:27.027Z",
                    establishment = "Cielo",
                    operationType = "DEBIT",
                    amount = 12.5
            ),
            Statement(dateHourTransaction = "2021-08-27T11:25:27.027Z",
                    establishment = "Cielo",
                    operationType = "DEBIT",
                    amount = 62.5
            )
    )

    private val statementResponse = CreditCardStatement(statements)

    @Mock
    lateinit var view: LastTransactionsContract.View

    @Mock
    lateinit var repository: StatementRepository

    @Mock
    lateinit var menuPreference: MenuPreference

    @Mock
    lateinit var userPreferences: UserPreferences

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: LastTransactionsPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token
        doReturn(EC_MOCK).whenever(menuPreference).getEC()

        presenter = LastTransactionsPresenter(view,
                repository,
                menuPreference,
                userPreferences,
                uiScheduler,
                ioScheduler
        )
    }

    @Test
    fun `Check the success return when fetchStatements is call`() {
        val captor = argumentCaptor<List<ExtratoTransicaoObj>>()

        val successPrepaid = Observable.just(statementResponse)

        doReturn(successPrepaid).whenever(repository)
                .statements(initialDt = "27/12/2021",
                        finalDt = "28/12/2021",
                        pageSize = 25,
                        page = 1,
                        merchantId = EC_MOCK,
                        accessToken = ACCESS_TOKEN_MOCK,
                        proxyCard = PROXY_MOCK
                )

        presenter.fetchStatements(initialDt = "27/12/2021",
                finalDt = "28/12/2021",
                proxyCard = PROXY_MOCK
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showTransactions(captor.capture())

        verify(view, never()).showError()
        verify(view, never()).showMessageNotTransactions()

        assertEquals(3, captor.firstValue.size)

        assertEquals("Cielo", captor.firstValue[0].time)
        assertEquals("Débito", captor.firstValue[0].description)
        assertEquals("R$ 23,50", captor.firstValue[0].amount)
        assertEquals("27/08/2021", captor.firstValue[0].status)
        assertEquals("2", captor.firstValue[0].statusCode)

        assertEquals("Cielo", captor.firstValue[1].time)
        assertEquals("Crédito", captor.firstValue[1].description)
        assertEquals("R$ 42,50", captor.firstValue[1].amount)
        assertEquals("27/08/2021", captor.firstValue[1].status)
        assertEquals("1", captor.firstValue[1].statusCode)

        assertEquals("Cielo", captor.firstValue[2].time)
        assertEquals("Débito", captor.firstValue[2].description)
        assertEquals("R$ 12,50", captor.firstValue[2].amount)
        assertEquals("27/08/2021", captor.firstValue[2].status)
        assertEquals("2", captor.firstValue[2].statusCode)
    }

    @Test
    fun `Check success return when list is empty in fetchStatements call`() {
        val successPrepaid = Observable.just(CreditCardStatement(listOf()))

        doReturn(successPrepaid).whenever(repository)
                .statements(initialDt = "27/12/2021",
                        finalDt = "28/12/2021",
                        pageSize = 25,
                        page = 1,
                        merchantId = EC_MOCK,
                        accessToken = ACCESS_TOKEN_MOCK,
                        proxyCard = PROXY_MOCK
                )

        presenter.fetchStatements(initialDt = "27/12/2021",
                finalDt = "28/12/2021",
                proxyCard = PROXY_MOCK
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showMessageNotTransactions()

        verify(view, never()).showError()
        verify(view, never()).showTransactions(any())
    }

    @Test
    fun `Check the error return when fetchStatements is call`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val error = Observable.error<RetrofitException>(exception)

        doReturn(error).whenever(repository)
                .statements(initialDt = "27/12/2021",
                        finalDt = "28/12/2021",
                        pageSize = 25,
                        page = 1,
                        merchantId = EC_MOCK,
                        accessToken = ACCESS_TOKEN_MOCK,
                        proxyCard = PROXY_MOCK
                )

        presenter.fetchStatements(initialDt = "27/12/2021",
                finalDt = "28/12/2021",
                proxyCard = PROXY_MOCK
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError()

        verify(view, never()).showTransactions(any())
        verify(view, never()).showMessageNotTransactions()
    }
}