package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MY_CARDS_PAYMENT
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MY_CARDS_TRANSFER
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val ACCESS_TOKEN_MOCK = "123456"
const val PROXY_MOCK = "000000000012345"

class CreditCardsPresenterTest {

    private val prepaid = "{\n" +
            "    \"status\":{\n" +
            "        \"description\":\"Conta provisória\",\n" +
            "        \"documentsAccepted\":false,\n" +
            "        \"allowDocumentUpload\":false,\n" +
            "        \"type\":\"TRANSITORY_ACCOUNT\"\n" +
            "    },\n" +
            "    \"cards\":[\n" +
            "        {\n" +
            "            \"number\":\"0977\",\n" +
            "            \"proxy\":\"000000000012345\",\n" +
            "            \"status\":{\n" +
            "                \"type\":\"ACTIVE\",\n" +
            "                \"description\":\"Ativo\",\n" +
            "                \"allowActivation\":false\n" +
            "            },\n" +
            "            \"issuer\":\"DOCK\"\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    private val balance = "{\n" +
            "    \"currency\":\"BRL\",\n" +
            "    \"amount\":0.0\n" +
            "}"

    @Mock
    lateinit var view: CreditCardsContract.CreditCardsView

    @Mock
    lateinit var creditCardsRepository: CreditCardsRepository

    @Mock
    lateinit var prepaidRepository: PrepaidRepository

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()
    private lateinit var presenter: CreditCardsPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(ACCESS_TOKEN_MOCK).whenever(userPreferences).token

        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(MY_CARDS_PAYMENT)
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(MY_CARDS_TRANSFER)

        presenter = CreditCardsPresenter(view,
                creditCardsRepository,
                prepaidRepository,
                userPreferences,
                featureTogglePreference,
                uiScheduler,
                ioScheduler
        )
    }

    @Test
    fun `Check 403 return when CATENO for digital account at fetchCardInformation`() {
        val errorMessage = ErrorMessage().apply {
            this.title = ""
            this.httpStatus = 403
            this.code = "403"
            this.errorCode = "ACCESS_DENIED_ISSUER"
            this.errorMessage = "You don't have permission to access this feature in this channel."
        }

        val exception = RetrofitException(
                null,
                null,
                APIUtils.createResponse(errorMessage),
                RetrofitException.Kind.HTTP,
                null,
                null,
                403)

        val error = Observable.error<RetrofitException>(exception)

        doReturn(error).whenever(prepaidRepository)
                .getUserStatusPrepago(ACCESS_TOKEN_MOCK)

        presenter.fetchCardInformation(false)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showErrorAccessDeniedIssuer()
    }

    @Test
    fun `check 403 return when access is denied for digital account in fetchCardInformation`() {
        val errorMessage = ErrorMessage().apply {
            title = ""
            httpStatus = 403
            code = "403"
            errorCode = "ACCESS_DENIED"
            errorMessage = "You don't have permission to access this feature."
        }

        val exception = RetrofitException(
                null,
                null,
                APIUtils.createResponse(errorMessage),
                RetrofitException.Kind.HTTP,
                null,
                null,
                403)

        val error = Observable.error<RetrofitException>(exception)

        doReturn(error).whenever(prepaidRepository)
                .getUserStatusPrepago(ACCESS_TOKEN_MOCK)

        presenter.fetchCardInformation(false)

        val captor = argumentCaptor<ErrorMessage>()

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showNotOwnerError(captor.capture())

        assertEquals(403, captor.firstValue.httpStatus)
        assertEquals("ACCESS_DENIED", captor.firstValue.errorCode)
        assertEquals("You don't have permission to access this feature.", captor.firstValue.message)
    }

    @Test
    fun `When isCardSuccess is false, the return of fetchCardInformation is successful, it type is TRANSITORY_ACCOUNT and the getUserCardBalance call is successful, call pix`() {
        val captor = argumentCaptor<PrepaidResponse>()

        val prepaidResponse =  Gson().fromJson(prepaid, PrepaidResponse::class.java)
        val balanceResponse =  Gson().fromJson(balance, PrepaidBalanceResponse::class.java)

        val successPrepaid = Observable.just(prepaidResponse)
        val successBalance = Observable.just(balanceResponse)

        doReturn(successPrepaid).whenever(prepaidRepository)
                .getUserStatusPrepago(ACCESS_TOKEN_MOCK)

        doReturn(successBalance).whenever(creditCardsRepository)
                .getUserCardBalance(PROXY_MOCK, ACCESS_TOKEN_MOCK)

        presenter.fetchCardInformation(false)
        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showPix(captor.capture())

        verify(view, never()).showError()
        verify(view, never()).showCardsInformation(any())

        assertEquals("false", captor.firstValue.status?.allowDocumentUpload)
        assertEquals("Conta provisória", captor.firstValue.status?.description)
        assertEquals("false", captor.firstValue.status?.documentsAccepted)
        assertEquals("TRANSITORY_ACCOUNT", captor.firstValue.status?.type)

        assertEquals(1, captor.firstValue.cards?.size)
        assertEquals("0977", captor.firstValue.cards?.get(0)?.cardNumber)
        assertEquals(PROXY_MOCK, captor.firstValue.cards?.get(0)?.proxyNumber)
        assertEquals("DOCK", captor.firstValue.cards?.get(0)?.issuer)
        assertEquals("ACTIVE", captor.firstValue.cards?.get(0)?.cardSituation?.type)
        assertEquals("Ativo", captor.firstValue.cards?.get(0)?.cardSituation?.situation)
    }

    @Test
    fun `When cardSituation type is different from WAITING_ACTIVATION call extract`() {
        val proxyCaptor = argumentCaptor<String>()
        val paymentCaptor = argumentCaptor<Boolean>()
        val transferCaptor = argumentCaptor<Boolean>()

        val prepaidResponse =  Gson().fromJson(prepaid, PrepaidResponse::class.java)

        presenter.showBottomFragmentProcess("TRANSITORY_ACCOUNT", prepaidResponse.cards?.first(), "DOCK")
        verify(view).showLastTransactions(proxyCaptor.capture(), transferCaptor.capture(), paymentCaptor.capture())

        verify(view, never()).startCardActivation(any())
        verify(view, never()).startCardSentSuccess()
        verify(view, never()).startCardReadProblemFWD()
        verify(view, never()).startCardProblemFWD()
        verify(view, never()).startCardProcessingFWD()
        verify(view, never()).startCardAccountFWD()

        assertEquals(PROXY_MOCK, proxyCaptor.firstValue)
        assertEquals(true, transferCaptor.firstValue)
        assertEquals(true, paymentCaptor.firstValue)
    }
}