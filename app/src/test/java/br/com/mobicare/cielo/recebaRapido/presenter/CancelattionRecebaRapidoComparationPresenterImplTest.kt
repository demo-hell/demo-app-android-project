package br.com.mobicare.cielo.recebaRapido.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.extensions.getPercent
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen.CancelattionRecebaRapidoComparationPresenterImpl
import br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen.CancelattionRecebaRapidoComparationView
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.taxaPlanos.mapper.ComparationViewModelRR
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper
import br.com.mobicare.cielo.taxasPlanos.presenter.ICON_BRAND_MOCK
import br.com.mobicare.cielo.taxasPlanos.presenter.MDR_MOCK
import br.com.mobicare.cielo.taxasPlanos.presenter.NAME_BRAND_MOCK
import br.com.mobicare.cielo.taxasPlanos.presenter.NAME_PRODUCT_MOCK
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations


const val FLEXIBLE_TERM_PAYMENT_MDR = 4.31
const val FLEXIBLE_TERM_PAYMENT_MDR_DEBIT = 2.0


class CancelattionRecebaRapidoComparationPresenterImplTest {

    private val conditionsInstallment: List<Condition> = listOf(Condition(
            anticipationAllowed = true,
            maximumInstallments = 2,
            mdr = MDR_MOCK,
            minimumInstallments = 12,
            minimumMDR = true,
            minimumMDRAmmount = 0.2,
            settlementTerm = 1,
            flexibleTerm = true,
            flexibleTermPaymentFactor = 0.0,
            flexibleTermPaymentMDR = FLEXIBLE_TERM_PAYMENT_MDR_DEBIT,
            flexibleTermPayment = FlexibleTermPayment(
                    contractedPeriod = "DAY_OF_WEEK",
                    frequency = listOf(1),
                    factor = 0.0,
                    mdr = 2.0),
            contractedMdrCommissionRate = 5.0,
            mdrContracted = 2.5,
            rateContractedRR = 2.5
            ))

    private val conditionsDebit: List<Condition> = listOf(Condition(
            anticipationAllowed = false,
            maximumInstallments = 0,
            mdr = MDR_MOCK,
            minimumInstallments = 0,
            minimumMDR = true,
            minimumMDRAmmount = 0.3,
            settlementTerm = 3,
            flexibleTerm = true,
            flexibleTermPaymentFactor = 0.0,
            flexibleTermPaymentMDR = FLEXIBLE_TERM_PAYMENT_MDR_DEBIT,
            flexibleTermPayment = FlexibleTermPayment(
                    contractedPeriod = null,
                    frequency = listOf(),
                    factor = 0.0,
                    mdr = 2.0),
            contractedMdrCommissionRate = 5.0,
            mdrContracted = 2.5,
            rateContractedRR = 2.5))

    private val conditionsCredit: List<Condition> = listOf(Condition(
            anticipationAllowed = true,
            maximumInstallments = 0,
            mdr = MDR_MOCK,
            minimumInstallments = 0,
            minimumMDR = true,
            minimumMDRAmmount = 0.2,
            settlementTerm = 1,
            flexibleTerm = true,
            flexibleTermPaymentFactor = 0.0,
            flexibleTermPaymentMDR = FLEXIBLE_TERM_PAYMENT_MDR,
            flexibleTermPayment = FlexibleTermPayment(
                    contractedPeriod = "DAY_OF_WEEK",
                    frequency = listOf(1),
                    factor = 0.0,
                    mdr = 4.31),
            contractedMdrCommissionRate = 5.0,
            mdrContracted = 2.5,
            rateContractedRR = 2.5))


    private val products = listOf(
        Product(
            conditions = conditionsCredit,
            name = NAME_PRODUCT_MOCK,
            prazoFlexivel = true,
            pixRateTax = 0.0
    ), Product(conditions = conditionsDebit,
            name = "Débito À vista",
            prazoFlexivel = false,
            pixRateTax = 0.0
    ), Product(conditions = conditionsInstallment,
            name = "Parcelado Loja",
            prazoFlexivel = true,
            pixRateTax = 0.0
    ))

    private val brands = listOf(Brand(code = 1,
            imgSource = ICON_BRAND_MOCK,
            name = NAME_BRAND_MOCK,
            products = products
    ))

    private val banks = listOf(Bank(accountDigit = null,
            accountNumber = "9999999000138",
            accountId = null,
            agency = "1234",
            agencyDigit = null,
            brands = brands,
            code = "983",
            imgSource = "https://digitalti.hdevelo.com.br/merchant/solutions/static/assets/img/banks/bank_0983.png",
            name = "CATENO",
            savingsAccount = false,
            digitalAccount = true
    ))

    private val solutions = listOf(Solution(banks = banks, name = "CATENO"))

    @Mock
    lateinit var view: CancelattionRecebaRapidoComparationView

    @Mock
    lateinit var repository: RecebaRapidoRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: CancelattionRecebaRapidoComparationPresenterImpl


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = CancelattionRecebaRapidoComparationPresenterImpl(view, repository, userPreferences, uiScheduler, ioScheduler)
    }

    @Test
    fun `Success on the call of the getTaxAndBrand`() {
        val returnSuccess = Observable.just(solutions)
        doReturn(returnSuccess).whenever(repository).getBrands(ACCESS_TOKEN_MOCK)

        presenter.getTaxAndBrand()

        verify(view).showLoading()
        verify(view).onTaxAndBrandSuccess(any())
    }

    @Test
    fun `Success on the validate mapper`() {
        val mapper = TaxAndBrandsMapper.mapper(solutions)

        val listWithoutRecebaRapido = ArrayList<ComponentLayoutFeeAndPlansItem>()
        val listWithRecebaRapido = ArrayList<ComponentLayoutFeeAndPlansItem>()
        val listWithoutRequestedScreen = ArrayList<ComponentLayoutFeeAndPlansItem>()

        listWithRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Débito", "em 3 dias a ${FLEXIBLE_TERM_PAYMENT_MDR_DEBIT.getPercent()}"))
        listWithRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Crédito", "toda Segunda-feira ${FLEXIBLE_TERM_PAYMENT_MDR.getPercent()}"))
        listWithRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Parcelado", "toda Segunda-feira ${FLEXIBLE_TERM_PAYMENT_MDR_DEBIT.getPercent()}"))

        listWithoutRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Débito", "em 3 dias a ${MDR_MOCK.getPercent()}"))
        listWithoutRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Crédito", "em 1 dia a ${MDR_MOCK.getPercent()}"))
        listWithoutRecebaRapido.add(ComponentLayoutFeeAndPlansItem("Parcelado", "em 1 dia a ${MDR_MOCK.getPercent()}"))

        val comparationViewModelRR = ComparationViewModelRR(
                listWithoutRecebaRapido,
                listWithRecebaRapido,
                listWithoutRequestedScreen
        )

        assertArrayEquals(mapper.listWithRR.toArray(), comparationViewModelRR.listWithRR.toArray())
        assertArrayEquals(mapper.listWithoutRR.toArray(), comparationViewModelRR.listWithoutRR.toArray())
        assertArrayEquals(mapper.listWithoutRequestedScreen.toArray(), comparationViewModelRR.listWithoutRequestedScreen.toArray())

    }

}