package br.com.mobicare.cielo.pix.ui.qrCode.charge

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.domain.QRCodeChargeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeChargeResponse
import br.com.mobicare.cielo.pix.ui.qrCode.charge.generate.PixGenerateQRCodeContract
import br.com.mobicare.cielo.pix.ui.qrCode.charge.generate.PixGenerateQRCodePresenter
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

private const val KEY_MOCK = "03868033000146"
private const val OTP_MOCK = "000000"
private const val DATE_MOCK = "2022-04-12"
private const val MESSAGE_MOCK = "Teste."
private const val IDENTIFIER_MOCK = "#Teste"

private const val AMOUNT_MOCK = 23.0

class PixGenerateQRCodePresenterTest {

    private val keysResponse = "{\n" +
            "    \"keys\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":11,\n" +
            "        \"keys\":[\n" +
            "            {\n" +
            "                \"key\":\"e0aeb7cc-823b-4cd5-8921-b9d1f95cf57e\",\n" +
            "                \"keyType\":\"EVP\",\n" +
            "                \"claimType\":\"PORTABILITY\",\n" +
            "                \"main\":true\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"03868033000146\",\n" +
            "                \"keyType\":\"CNPJ\",\n" +
            "                \"main\":false\n" +
            "            },\n" +
            "            {\n" +
            "                \"key\":\"ec5ff66a-acf8-468b-b69f-e85448152004\",\n" +
            "                \"keyType\":\"EVP\",\n" +
            "                \"main\":false\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"claims\":{\n" +
            "        \"date\":\"2022-03-17 16:29:26\",\n" +
            "        \"count\":0,\n" +
            "        \"keys\":[\n" +
            "            \n" +
            "        ]\n" +
            "    }\n" +
            "}"

    private val qrCodeResponse = "{\n" +
            "    \"txId\":\"#Teste\",\n" +
            "    \"key\":\"03868033000146\",\n" +
            "    \"originalAmount\":23.0,\n" +
            "    \"message\":\"Teste.\",\n" +
            "    \"expirationDate\":\"2022-04-13\"\n" +
            "}"

    @Mock
    lateinit var view: PixGenerateQRCodeContract.View

    @Mock
    lateinit var repository: PixQRCodeRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixGenerateQRCodePresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixGenerateQRCodePresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `When getUsername is called and value is empty, the return must be empty`() {
        doReturn("").whenever(userPreferences).userName
        assertEquals("", presenter.getUsername())
    }

    @Test
    fun `When getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("loja1").whenever(userPreferences).userName
        assertEquals("loja1", presenter.getUsername())
    }

    @Test
    fun `When onValidateKey is called, it needs to get the first item that has ClaimType different from PORTABILITY and OWNERSHIP of key list`() {
        assertEquals("03.868.033/0001-46", presenter.onValidateKey(keys()))
    }

    @Test
    fun `when call onRevokeClaim and return a success show onSuccessClaim and onSuccessRevokeClaim`() {
        val captorOTP = argumentCaptor<String>()
        val captorRequest = argumentCaptor<QRCodeChargeRequest>()
        val captorResponse = argumentCaptor<QRCodeChargeResponse>()

        val response = Gson().fromJson(qrCodeResponse, QRCodeChargeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .chargeQRCode(captorOTP.capture(), captorRequest.capture())

        Mockito.`when`(view.onErrorGenerateQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onValidateKey(keys())
        presenter.onGenerateQRCode(
            amount = AMOUNT_MOCK,
            message = MESSAGE_MOCK,
            expirationDate = DATE_MOCK,
            identifier = IDENTIFIER_MOCK,
            otp = OTP_MOCK
        )

        verify(view).onSuccessGenerateQRCode(captorResponse.capture())
        verify(view, never()).showError(any())

        assertEquals(OTP_MOCK, captorOTP.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(AMOUNT_MOCK, captorRequest.firstValue.originalAmount)
        assertEquals(MESSAGE_MOCK, captorRequest.firstValue.message)
        assertEquals(DATE_MOCK, captorRequest.firstValue.expirationDate)
        assertEquals(IDENTIFIER_MOCK, captorRequest.firstValue.txId)
        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `when call onGenerateQRCode and return an error show onSuccessClaim and onSuccessRevokeClaim`() {
        val captorOTP = argumentCaptor<String>()
        val captorError = argumentCaptor<ErrorMessage>()
        val captorRequest = argumentCaptor<QRCodeChargeRequest>()

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
        doReturn(errorObservable).whenever(repository)
            .chargeQRCode(captorOTP.capture(), captorRequest.capture())

        Mockito.`when`(view.onErrorGenerateQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onValidateKey(keys())
        presenter.onGenerateQRCode(
            amount = AMOUNT_MOCK,
            message = MESSAGE_MOCK,
            expirationDate = DATE_MOCK,
            identifier = IDENTIFIER_MOCK,
            otp = OTP_MOCK
        )

        verify(view).showError(captorError.capture())


        verify(view, never()).onSuccessGenerateQRCode(any())

        assertEquals(OTP_MOCK, captorOTP.firstValue)
        assertEquals(KEY_MOCK, captorRequest.firstValue.key)
        assertEquals(AMOUNT_MOCK, captorRequest.firstValue.originalAmount)
        assertEquals(MESSAGE_MOCK, captorRequest.firstValue.message)
        assertEquals(DATE_MOCK, captorRequest.firstValue.expirationDate)
        assertEquals(IDENTIFIER_MOCK, captorRequest.firstValue.txId)

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    private fun keys(): List<PixKeysResponse.KeyItem>? =
        Gson().fromJson(keysResponse, PixKeysResponse::class.java).keys?.keys

}