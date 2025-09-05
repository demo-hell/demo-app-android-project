package br.com.mobicare.cielo.taxasPlanos.presenter.meuAluguel

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_POSTECIPADO
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.api.PostecipadoRepository
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoMeuAluguelContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoMeuAluguelPresenter
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val MESSAGE_FEATURE_TOGGLE = "Feature em manutenção"

class PostecipadoMeuAluguelPresenterTest {

    private val jsonRentInformation = "[\n" +
            "   {\n" +
            "      \"currentDate\":\"2021-11-07T04:00:00\",\n" +
            "      \"dateUpdate\":\"2021-11-07T04:00:00\",\n" +
            "      \"expirationDate\":\"2023-05-05\",\n" +
            "      \"referenceMonth\":\"2022-05-05\",\n" +
            "      \"valueContract\":3600,\n" +
            "      \"valueDiscountNegotiated\":900,\n" +
            "      \"valueDiscountPartial\":320,\n" +
            "      \"percentageReached\":\"20\",\n" +
            "      \"percentageMissing\":\"80\",\n" +
            "      \"percentageDiscountPartial\":\"20\",\n" +
            "      \"percentageDiscountNegotiated\":\"60\",\n" +
            "      \"billingPerformed\":2500,\n" +
            "      \"missingValue\":900,\n" +
            "      \"isWaitingPeriod\":false,\n" +
            "      \"isExempted\":false,\n" +
            "      \"terminals\":[\n" +
            "         {\n" +
            "            \"terminalCode\":\"14\",\n" +
            "            \"terminalName\":\"POS\",\n" +
            "            \"valueDiscountNegotiated\":120,\n" +
            "            \"percentageDiscountNegotiated\":20,\n" +
            "            \"valueDiscountPartial\":100,\n" +
            "            \"percentageDiscountPartial\":30,\n" +
            "            \"terminalQuantity\":\"1\"\n" +
            "         }\n" +
            "      ]\n" +
            "   }\n" +
            "]"


    private val featureToggleObj = Feature(featureName = FeatureTogglePreference.POSTECIPADO,
            show = true,
            status = "activated",
            statusMessage = null
    )

    private val ioScheduler = Schedulers.trampoline()
    private val uiScheduler = Schedulers.trampoline()

    @Mock
    private lateinit var view: PostecipadoMeuAluguelContract.View

    @Mock
    private lateinit var repository: PostecipadoRepository

    @Mock
    private lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: PostecipadoMeuAluguelPresenter
    private lateinit var rentInformationResponse: PlanInformationResponse

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        doReturn(featureToggleObj).whenever(featureTogglePreference).getFeatureToggleObject(
                FeatureTogglePreference.POSTECIPADO
        )

        presenter = PostecipadoMeuAluguelPresenter(view, repository, featureTogglePreference, uiScheduler, ioScheduler)
    }

    @Test
    fun `success on get rent information`() {
        rentInformationResponse =
                Gson().fromJson(jsonRentInformation, PlanInformationResponse::class.java)

        val successResponse = Observable.just(rentInformationResponse)
        val argumentCaptor = argumentCaptor<PlanInformationResponse>()
        val argumentRentAmount = argumentCaptor<Double>()

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
        val machine = TaxaPlanosSolutionResponse(pos = listOf(feeMachine, feeMachine),
                mobile = listOf(feeMachine)
        )

        doReturn(successResponse).`when`(repository).getPlanInformation(TAXA_PLANOS_POSTECIPADO)

        presenter.loadRentInformation(machine)

        verify(view).hideLoading()
        verify(view, never()).notEligibleForPostecipate()
        verify(view).showRentInformation(argumentCaptor.capture(), argumentRentAmount.capture())
        verify(view, never()).showError(any())
        verify(view, never()).unavailableService(any())

        val currentRentInfo = argumentCaptor.firstValue[0]
        val terminal = currentRentInfo.terminals?.get(0)

        assertEquals("2021-11-07T04:00:00", currentRentInfo.currentDate)
        assertEquals("2021-11-07T04:00:00", currentRentInfo.dateUpdate)
        assertEquals("2022-05-05", currentRentInfo.referenceMonth)
        assertEquals("2023-05-05", currentRentInfo.expirationDate)
        assertEquals(3600.00, currentRentInfo.valueContract!!, 0.0)
        assertEquals("20", currentRentInfo.percentageDiscountPartial)
        assertEquals(320.00, currentRentInfo.valueDiscountPartial!!, 0.0)
        assertEquals(900.00, currentRentInfo.valueDiscountNegotiated!!, 0.0)
        assertEquals("60", currentRentInfo.percentageDiscountNegotiated)
        assertEquals("20", currentRentInfo.percentageReached)
        assertEquals("80", currentRentInfo.percentageMissing)
        assertEquals(2500.00, currentRentInfo.billingPerformed!!, 0.0)
        assertEquals(900.00, currentRentInfo.missingValue!!, 0.0)

        assertEquals("14", terminal?.terminalCode)
        assertEquals("POS", terminal?.terminalName)
        assertEquals(1, terminal?.terminalQuantity)
        assertEquals(120.00, terminal?.valueDiscountNegotiated)
        assertEquals("20", terminal?.percentageDiscountNegotiated)
        assertEquals(100.00, terminal?.valueDiscountPartial)
        assertEquals("30", terminal?.percentageDiscountPartial)

        assertEquals(81.6, argumentRentAmount.firstValue, 0.0)
    }

    @Test
    fun `return error code 420 when loadRentInformation is calling`() {
        val exception = RetrofitException(
                message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.HTTP,
                exception = null,
                retrofit = null,
                httpStatus = 420
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).getPlanInformation(TAXA_PLANOS_POSTECIPADO)

        presenter.loadRentInformation(any())

        verify(view).hideLoading()
        verify(view).notEligibleForPostecipate()
        verify(view, never()).showRentInformation(any(), any())
        verify(view, never()).showError(any())
        verify(view, never()).unavailableService(any())
    }

    @Test
    fun `return error code other than 420 when loadRentInformation is calling`() {
        val exception = RetrofitException(
                message = null,
                url = null,
                response = null,
                kind = RetrofitException.Kind.NETWORK,
                exception = null,
                retrofit = null,
                httpStatus = 500
        )

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository).getPlanInformation(TAXA_PLANOS_POSTECIPADO)

        presenter.loadRentInformation(any())

        verify(view).hideLoading()
        verify(view).showError(any())
        verify(view, never()).showRentInformation(any(), any())
        verify(view, never()).notEligibleForPostecipate()
        verify(view, never()).unavailableService(any())
    }


    @Test
    fun `return error of unavailable service when feature toggle is false`() {
        featureToggleObj.show = false
        featureToggleObj.statusMessage = MESSAGE_FEATURE_TOGGLE
        doReturn(featureToggleObj).whenever(featureTogglePreference).getFeatureToggleObject(
                FeatureTogglePreference.POSTECIPADO
        )
        val captorMessage = argumentCaptor<String>()

        presenter.loadRentInformation(any())

        verify(view).unavailableService(captorMessage.capture())
        verify(view, never()).showError(any())
        verify(view, never()).showRentInformation(any(), any())
        verify(view, never()).notEligibleForPostecipate()

        assertEquals(MESSAGE_FEATURE_TOGGLE, captorMessage.firstValue)
    }
}