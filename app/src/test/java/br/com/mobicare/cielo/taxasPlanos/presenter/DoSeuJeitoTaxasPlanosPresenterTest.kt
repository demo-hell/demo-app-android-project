package br.com.mobicare.cielo.taxasPlanos.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import br.com.mobicare.cielo.meuCadastroNovo.domain.Condition
import br.com.mobicare.cielo.meuCadastroNovo.domain.Product
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.services.presenter.ACCESS_TOKEN_MOCK
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.doSeuJeito.DoSeuJeitoTaxasPlanosPresenter
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas.DoSeuJeitoTaxasPlanosContract
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DoSeuJeitoTaxasPlanosPresenterTest {

    private val conditions: List<Condition> = listOf(
        Condition(
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
            contractedMdrCommissionRate = 5.0,
            mdrContracted = 2.5,
            rateContractedRR = 2.5
        )
    )

    private val products = listOf(
        Product(
            conditions = conditions,
            name = NAME_PRODUCT_MOCK,
            prazoFlexivel = true,
            pixRateTax = 0.0
        )
    )

    private val brands = listOf(
        Brand(
            code = 1,
            imgSource = ICON_BRAND_MOCK,
            name = NAME_BRAND_MOCK,
            products = products
        )
    )

    private val banks = listOf(
        Bank(
            accountDigit = null,
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
        )
    )

    private val solutions = listOf(Solution(banks = banks, name = "CATENO"))

    @Mock
    lateinit var view: DoSeuJeitoTaxasPlanosContract.View

    @Mock
    lateinit var taxaPlanoRepository: TaxaPlanoRepository

    @Mock
    lateinit var recebaRapidoRepository: RecebaRapidoRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: DoSeuJeitoTaxasPlanosPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        presenter = spy(
            DoSeuJeitoTaxasPlanosPresenter(
                view,
                taxaPlanoRepository,
                recebaRapidoRepository,
                userPreferences,
                uiScheduler,
                ioScheduler,
                featureTogglePreference
            )
        )
    }

    @Test
    fun `Success on the load taxes`() {
        val returnSuccess = Observable.just(solutions)
        doReturn(returnSuccess).whenever(recebaRapidoRepository).getBrands(ACCESS_TOKEN_MOCK)

        presenter.loadTaxes()
        verify(view).showTaxes(any())
    }

    @Test
    fun `Success on loadMachines call`() {
        val captorLoading = argumentCaptor<Boolean>()
        val captorResponse = argumentCaptor<TaxaPlanosSolutionResponse>()

        val feeMachine = TaxaPlanosMachine(
            model = "",
            logicalNumber = "123",
            logicalNumberDigit = "1",
            rentalAmount = 40.8,
            name = "Cielo",
            description = "",
            technology = "",
            replacementAllowed = true
        )
        val response = TaxaPlanosSolutionResponse(
            pos = listOf(feeMachine, feeMachine),
            mobile = listOf(feeMachine)
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosSolutionResponse, String>).onSuccess(
                response
            )
        }.whenever(taxaPlanoRepository).loadMachine(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        presenter.loadMachines()

        verify(view).showMachinesLoading(captorLoading.capture())
        verify(view).showMachine(captorResponse.capture())
        verify(view, never()).hideMachinesCard()

        assertEquals(false, captorLoading.firstValue)
        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `Error on loadMachines call`() {
        val captorLoading = argumentCaptor<Boolean>()
        val captorError = argumentCaptor<ErrorMessage>()

        val exception = RetrofitException(
            message = null,
            url = null,
            response = null,
            kind = RetrofitException.Kind.NETWORK,
            exception = null,
            retrofit = null,
            httpStatus = 500
        )

        doAnswer {
            (it.arguments[1] as APICallbackDefault<TaxaPlanosSolutionResponse, String>).onError(
                APIUtils.convertToErro(exception)
            )
        }.whenever(taxaPlanoRepository).loadMachine(
            token = eq(ACCESS_TOKEN_MOCK),
            callback = any()
        )

        presenter.loadMachines()

        verify(view).showMachinesLoading(captorLoading.capture())
        verify(view).showMachineError(captorError.capture())
        verify(view, never()).showMachine(any())

        assertEquals(false, captorLoading.firstValue)
        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `When FT is activated should show RA Whatsapp dialog`() {
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)
        doReturn(Feature(statusMessage = "whatsappUrl")).whenever(featureTogglePreference).getFeatureToggleObject(FeatureTogglePreference.RA_CANCEL_WHATSAPP_ONLY)

        val captorUrl = argumentCaptor<String>()
        presenter.confirmCancellation()
        verify(view).showWhatsAppCancellationDialog(captorUrl.capture())
        assertEquals("whatsappUrl", captorUrl.firstValue)
    }
}