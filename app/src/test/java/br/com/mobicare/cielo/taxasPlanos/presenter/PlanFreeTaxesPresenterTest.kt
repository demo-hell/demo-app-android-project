package br.com.mobicare.cielo.taxasPlanos.presenter

import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.BandeiraModelView
import br.com.mobicare.cielo.taxaPlanos.componentes.taxas.TaxasModelView
import br.com.mobicare.cielo.taxaPlanos.mapper.TaxAndBrandsMapper
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.PlanFreeTaxesPresenterContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.presenter.PlanFreeTaxesPresenter
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

const val ICON_BRAND_MOCK = "https://digitalti.hdevelo.com.br/merchant/solutions/static/assets/img/brands/brand_1.png"
const val NAME_BRAND_MOCK = "Visa"
const val NAME_PRODUCT_MOCK = "Crédito À vista"
const val MINIMUM_MDRA_AMMOUNT_MOCK = 0.2
const val MDR_MOCK = 2.0
const val SETTLEMENT_TERM_MOCK = 1
const val FLEXIBLE_TERM_PAYMENT_MDR_MOCK = 4.31
const val MDR_CONTRACTED_MOCK = 2.5
const val RR_CONTRACTED_MOCK = 2.5
const val COMMISSION_CONTRACTED_MOCK = 5.0

class PlanFreeTaxesPresenterTest {

    private val conditions: List<Condition> = listOf(Condition(
            anticipationAllowed = true,
            maximumInstallments = 0,
            mdr = MDR_MOCK,
            minimumInstallments = 0,
            minimumMDR = true,
            minimumMDRAmmount = MINIMUM_MDRA_AMMOUNT_MOCK,
            settlementTerm = SETTLEMENT_TERM_MOCK,
            flexibleTerm = false,
            flexibleTermPaymentFactor = 0.0,
            flexibleTermPaymentMDR = FLEXIBLE_TERM_PAYMENT_MDR_MOCK,
            flexibleTermPayment = null,
            contractedMdrCommissionRate = COMMISSION_CONTRACTED_MOCK,
            mdrContracted = MDR_CONTRACTED_MOCK,
            rateContractedRR = RR_CONTRACTED_MOCK))

    private val products = listOf(
        Product(
            conditions = conditions,
            name = NAME_PRODUCT_MOCK,
            prazoFlexivel = true,
            pixRateTax = 0.0
        )
    )

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
    lateinit var view: PlanFreeTaxesPresenterContract.View

    @Mock
    lateinit var recebaRapidoRepository: RecebaRapidoRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: PlanFreeTaxesPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = PlanFreeTaxesPresenter(view, uiScheduler, ioScheduler, recebaRapidoRepository, userPreferences)
    }

    private fun getConditionGetReceive(contractedPeriod: String?, frequency: List<Int>, max: Int = 0, min: Int = 0): Condition {
        val flexibleTermPayment = FlexibleTermPayment(contractedPeriod = contractedPeriod, frequency = frequency, factor = 0.0, mdr = 4.31)

        return Condition(
                anticipationAllowed = true,
                maximumInstallments = max,
                mdr = MDR_MOCK,
                minimumInstallments = min,
                minimumMDR = true,
                minimumMDRAmmount = MINIMUM_MDRA_AMMOUNT_MOCK,
                settlementTerm = SETTLEMENT_TERM_MOCK,
                flexibleTerm = true,
                flexibleTermPaymentFactor = 0.0,
                flexibleTermPaymentMDR = FLEXIBLE_TERM_PAYMENT_MDR_MOCK,
                flexibleTermPayment = flexibleTermPayment,
                contractedMdrCommissionRate = COMMISSION_CONTRACTED_MOCK,
                mdrContracted = MDR_CONTRACTED_MOCK,
                rateContractedRR = RR_CONTRACTED_MOCK
        )
    }

    @Test
    fun `Success on the fetch all supported brands`() {
        val returnSuccess = Observable.just(solutions)
        doReturn(returnSuccess).whenever(recebaRapidoRepository).getBrands(ACCESS_TOKEN_MOCK)

        presenter.fetchAllSupportedBrands()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).loadBrands(any())
    }

    @Test
    fun `Error on the fetch all supported brands`() {
        val exception = RetrofitException(message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500)

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(recebaRapidoRepository).getBrands(ACCESS_TOKEN_MOCK)

        presenter.fetchAllSupportedBrands()
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(any())
    }

    @Test
    fun `Verify name brand in BandeiraModelView`() {
        val mapper = TaxAndBrandsMapper.convert(solutions)
        assertEquals(NAME_BRAND_MOCK, mapper[0].nomeBandeira)
    }

    @Test
    fun `Verify icon brand in BandeiraModelView`() {
        val mapper = TaxAndBrandsMapper.convert(solutions)
        assertEquals(ICON_BRAND_MOCK, mapper[0].iconeBandeira)
    }

    @Test
    fun `Verify taxes in BandeiraModelView`() {
        val mapper = TaxAndBrandsMapper.convert(solutions)
        val taxes = ArrayList<TaxasModelView>()
        val valuesTaxes = ArrayList<Pair<String, String>>()

        valuesTaxes.add(Pair("Recebimento", "$SETTLEMENT_TERM_MOCK ${Text.DAY}"))
        valuesTaxes.add(Pair(Text.TAX_MINIMUM, MINIMUM_MDRA_AMMOUNT_MOCK.toPtBrRealString()))
        val commission = COMMISSION_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val mdr = MDR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val rr = RR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        valuesTaxes.add(Pair(TaxAndBrandsMapper.RATE_MDR_RR, "$commission% ($mdr% + $rr%)"))
        taxes.add(TaxasModelView(name = NAME_PRODUCT_MOCK, values = valuesTaxes))

        assertEquals(taxes, mapper[0].taxas)
    }

    @Test
    fun `Verify BandeiraModelView Credit`() {
        val mapper = TaxAndBrandsMapper.convert(solutions)
        val taxes = ArrayList<TaxasModelView>()
        val valuesTaxes = ArrayList<Pair<String, String>>()

        valuesTaxes.add(Pair("Recebimento", "$SETTLEMENT_TERM_MOCK ${Text.DAY}"))
        valuesTaxes.add(Pair(Text.TAX_MINIMUM, MINIMUM_MDRA_AMMOUNT_MOCK.toPtBrRealString()))
        val commission = COMMISSION_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val mdr = MDR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val rr = RR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        valuesTaxes.add(Pair(TaxAndBrandsMapper.RATE_MDR_RR, "$commission% ($mdr% + $rr%)"))
        taxes.add(TaxasModelView(name = NAME_PRODUCT_MOCK, values = valuesTaxes))

        assertEquals(BandeiraModelView(NAME_BRAND_MOCK, ICON_BRAND_MOCK, taxes), mapper[0])
    }

    @Test
    fun `Verify BandeiraModelView Installment`() {
        val condition = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(6), max = 12, min = 2)

        val products = listOf(
            Product(
                conditions = listOf(condition),
                name = "Parcelado Loja",
                prazoFlexivel = true,
                pixRateTax = 0.0
            )
        )

        val brands = listOf(Brand(code = 1,
                imgSource = ICON_BRAND_MOCK,
                name = NAME_BRAND_MOCK,
                products = products
        ))

        val banks = listOf(Bank(accountDigit = null,
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

        val solutions = listOf(Solution(banks = banks, name = "CATENO"))

        val mapper = TaxAndBrandsMapper.convert(solutions)
        val taxes = ArrayList<TaxasModelView>()
        val valuesTaxes = ArrayList<Pair<String, String>>()

        valuesTaxes.add(Pair("Recebimento", "todo Sábado"))
        valuesTaxes.add(Pair(Text.TAX_MINIMUM, MINIMUM_MDRA_AMMOUNT_MOCK.toPtBrRealString()))
        valuesTaxes.add(Pair(TaxAndBrandsMapper.INSTALLMENTS, TaxAndBrandsMapper.RATES_MDR_RR))
        val commission = COMMISSION_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val mdr = MDR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        val rr = RR_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        valuesTaxes.add(Pair("${Text.FROM} 2${Text.TO} 12${Text.X}", "$commission% ($mdr% + $rr%)"))
        taxes.add(TaxasModelView(name = "Parcelado Loja", values = valuesTaxes))

        assertEquals(BandeiraModelView(NAME_BRAND_MOCK, ICON_BRAND_MOCK, taxes), mapper[0])
    }

    @Test
    fun `Verify getReceive when receive three times a Month`() {
        val conditionMonth = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.MONTH, frequency = listOf(1, 2, 3))
        assertEquals(Pair("Recebimento", "1, 2 e 3 de cada mês"), TaxAndBrandsMapper.getReceive(conditionMonth))
    }

    @Test
    fun `Verify getReceive when receive two times a Month`() {
        val conditionMonth = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.MONTH, frequency = listOf(1, 3))
        assertEquals(Pair("Recebimento", "1 e 3 de cada mês"), TaxAndBrandsMapper.getReceive(conditionMonth))
    }

    @Test
    fun `Verify getReceive when is Sunday`() {
        val conditionWeek = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(0))
        assertEquals(Pair("Recebimento", "todo Domingo"), TaxAndBrandsMapper.getReceive(conditionWeek))
    }

    @Test
    fun `Verify getReceive when is Monday`() {
        val conditionWeek = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(1))
        assertEquals(Pair("Recebimento", "toda Segunda-feira"), TaxAndBrandsMapper.getReceive(conditionWeek))
    }

    @Test
    fun `Verify getReceive when is Saturday`() {
        val conditionWeek = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(6))
        assertEquals(Pair("Recebimento", "todo Sábado"), TaxAndBrandsMapper.getReceive(conditionWeek))
    }

    @Test
    fun `Verify getReceive when is for receive in 2 days`() {
        val condition = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.DAILY, frequency = listOf(2))
        assertEquals(Pair("Recebimento", "em 2 dias"), TaxAndBrandsMapper.getReceive(condition))
    }

    @Test
    fun `Verify getReceive when is for receive in 12 hours`() {
        val condition = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.DAILY, frequency = listOf(0))
        assertEquals(Pair(Text.RECEBIMENTO, "0 dia"), TaxAndBrandsMapper.getReceive(condition))
    }

    @Test
    fun `Verify getReceive when contracted period is not mapped`() {
        val condition = getConditionGetReceive(contractedPeriod = "", frequency = listOf(0))
        assertEquals(Pair(Text.RECEBIMENTO, Text.SIMPLE_LINE), TaxAndBrandsMapper.getReceive(condition))
    }

    @Test
    fun `Verify getReceive when contracted period is null`() {
        val condition = getConditionGetReceive(contractedPeriod = null, frequency = listOf(0))
        assertEquals(Pair(Text.RECEBIMENTO, Text.SIMPLE_LINE), TaxAndBrandsMapper.getReceive(condition))
    }

    @Test
    fun `Verify groupParceladoLoja when minimum is different of maximum`() {
        val condition = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(6), max = 12, min = 2)
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("Recebimento", "todo Sábado"))
        list.add(Pair(Text.TAX_MINIMUM, MINIMUM_MDRA_AMMOUNT_MOCK.toPtBrRealString()))
        list.add(Pair(TaxAndBrandsMapper.INSTALLMENTS, TaxAndBrandsMapper.RATES_MDR))
        val commission = COMMISSION_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        list.add(Pair("${Text.FROM} 2${Text.TO} 12${Text.X}", "$commission%"))

        assertEquals(list, TaxAndBrandsMapper.groupParceladoLoja(lastProductName = "", condition = condition, false))
    }

    @Test
    fun `Verify groupParceladoLoja when minimum and maximum is on equal`() {
        val condition = getConditionGetReceive(contractedPeriod = TaxAndBrandsMapper.WEEK, frequency = listOf(6), max = 2, min = 2)
        val list = ArrayList<Pair<String, String>>()
        list.add(Pair("Recebimento", "todo Sábado"))
        list.add(Pair(Text.TAX_MINIMUM, MINIMUM_MDRA_AMMOUNT_MOCK.toPtBrRealString()))
        list.add(Pair(TaxAndBrandsMapper.INSTALLMENTS, TaxAndBrandsMapper.RATES_MDR))
        val commission = COMMISSION_CONTRACTED_MOCK.toPtBrRealStringWithoutSymbol()
        list.add(Pair("2${Text.X}", "$commission%"))

        assertEquals(list, TaxAndBrandsMapper.groupParceladoLoja(lastProductName = "", condition = condition, false))
    }

}