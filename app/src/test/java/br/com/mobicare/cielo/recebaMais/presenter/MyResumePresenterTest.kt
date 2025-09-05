package br.com.mobicare.cielo.recebaMais.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.recebaMais.domains.entities.*
import br.com.mobicare.cielo.recebaMais.managers.MyResumeRepository
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumeContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumePresenter
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MyResumePresenterTest {

    private val bank = BankAccountDetails(account = "103921787",
            accountDigit = null,
            agency = "5485",
            agencyDigit = null,
            code = 237,
            name = "BANCO BRADESCO S.A")

    private val listInstallment = listOf(InstallmentDetails(
            amountOwed = 2030.57,
            dueDate = "2020-05-14",
            installmentAmount = 2030.57,
            installmentNumber = 1,
            lastPayment = null,
            status = "Pago via receba mais",
            statusCode = 2
    ), InstallmentDetails(
            amountOwed = 2030.57,
            dueDate = "2020-06-14",
            installmentAmount = 2030.57,
            installmentNumber = 2,
            lastPayment = null,
            status = "Em aberto",
            statusCode = 3
    ))

    private val partner = PartnerDetails(code = "2007003656",
            merchantId = null,
            name = "Money Plus",
            score = null
    )

    private val contract = ContractDetails(annualEffectiveCostRate = 45.72,
            annualInterestRate = 42.41,
            bankAccount = bank,
            contractCode = "67365",
            contractDate = "2020-04-14",
            customerId = "1",
            installmentAmount = 2030.57,
            installments = listInstallment,
            interestRate = 2.99,
            iofRate = 0.0,
            mechantId = "2012458534",
            monthlyEffectiveCostRate = 3.19,
            partner = partner,
            paymentFirstInstallmentDate = "2020-05-14",
            quantity = 2,
            registrationFee = 298.88,
            status = "Ativo",
            statusCode = 0,
            valueContract = 19925.46
    )

    @Mock
    lateinit var view: MyResumeContract.View

    @Mock
    lateinit var repository: MyResumeRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var response: ContractDetailsResponse
    private lateinit var presenter: MyResumePresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        response = ContractDetailsResponse(listOf(contract))
        presenter = MyResumePresenter(view, repository, userPreferences, uiScheduler, ioScheduler)
    }

    @Test
    fun `When calling loadDetails return is an success`() {
        val returnSuccess = Observable.just(response)
        doReturn(returnSuccess).whenever(repository).getContractsDetails(ACCESS_TOKEN_MOCK)

        presenter.loadDetails()

        val captor = argumentCaptor<ContractDetails>()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showContract(captor.capture())

        assertEquals(captor.firstValue.installments.size, 2)
        assertEquals(captor.firstValue.quantity, 2)
        assertEquals(captor.firstValue.status, "Ativo")
        assertEquals(captor.firstValue.valueContract, 19925.46, Double.MIN_VALUE)
        assertEquals(captor.firstValue.paymentFirstInstallmentDate, "2020-05-14")
        assertEquals(captor.firstValue.contractDate, "2020-04-14")
        assertEquals(captor.firstValue.mechantId, "2012458534")
        assertEquals(captor.firstValue.installmentAmount, 2030.57, Double.MIN_VALUE)
        assertEquals(captor.firstValue.annualInterestRate, 42.41, Double.MIN_VALUE)
        assertEquals(captor.firstValue.installments[0].statusCode, 3)
        assertEquals(captor.firstValue.installments[1].statusCode, 2)
        assertEquals(captor.firstValue.installments[0].dueDate, "2020-06-14")
        assertEquals(captor.firstValue.installments[1].dueDate, "2020-05-14")
        assertEquals(captor.firstValue.partner.code, "2007003656")
        assertEquals(captor.firstValue.partner.name, "Money Plus")
        assertEquals(captor.firstValue.bankAccount.name, "BANCO BRADESCO S.A")
        assertEquals(captor.firstValue.bankAccount.code, 237)
        assertEquals(captor.firstValue.bankAccount.account, "103921787")
        assertEquals(captor.firstValue.bankAccount.agency, "5485")
    }

    @Test
    fun `When calling loadDetails return is an exception`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(repository).getContractsDetails(ACCESS_TOKEN_MOCK)

        presenter.loadDetails()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(anyOrNull())
    }
}