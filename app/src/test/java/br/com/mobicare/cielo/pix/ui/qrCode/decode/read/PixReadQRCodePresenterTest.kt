package br.com.mobicare.cielo.pix.ui.qrCode.decode.read

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

const val QRCODE_MOCK =
    "00020126600014br.gov.bcb.pix0114+55629812457960220infoadicionalexample5204000053039865802BR5906Wildes6006Cidade62150511txidexample63041C49"

class PixReadQRCodePresenterTest {

    private val decodeResponse = "{\n" +
            "    \"type\":\"STATIC\",\n" +
            "    \"operationType\":\"CHARGE\",\n" +
            "    \"endToEndId\":\"64d3108b-9d6c-46d2-b5b6-67c1084bda3e\",\n" +
            "    \"participant\":1027058,\n" +
            "    \"participantName\":\"CIELO S.A.\",\n" +
            "    \"receiverName\":\"MASSA DADOS AFIL. - 237-1\",\n" +
            "    \"receiverPersonType\":\"LEGAL_PERSON\",\n" +
            "    \"receiverDocument\":\"77795766000134\",\n" +
            "    \"conciliationId\":\"txidexample\",\n" +
            "    \"city\":\"Cidade\",\n" +
            "    \"branch\":\"1\",\n" +
            "    \"accountType\":\"3\",\n" +
            "    \"accountNumber\":\"7547520001\",\n" +
            "    \"key\":\"+5562981245796\",\n" +
            "    \"keyType\":\"PHONE\",\n" +
            "    \"category\":\"0000\",\n" +
            "    \"additionalData\":\"infoadicionalexample\"\n" +
            "}"

    @Mock
    lateinit var view: PixReadQRCodeContract.View

    @Mock
    lateinit var repository: PixQRCodeRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixReadQRCodePresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixReadQRCodePresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `When isFirstTimeAskCameraPermission is called and value is false, the return must be false`() {
        doReturn(false).whenever(userPreferences).cameraPermissionCheck
        assertEquals(false, presenter.isFirstTimeAskCameraPermission())
    }

    @Test
    fun `When isFirstTimeAskCameraPermission is called and value is true, the return must be true`() {
        doReturn(true).whenever(userPreferences).cameraPermissionCheck
        assertEquals(true, presenter.isFirstTimeAskCameraPermission())
    }

    @Test
    fun `when calling onValidateQRCode and the return is success, it shows onSuccessValidateQRCode`() {
        val captorRequest = argumentCaptor<QRCodeDecodeRequest>()
        val captorResponse = argumentCaptor<QRCodeDecodeResponse>()

        val response = Gson().fromJson(decodeResponse, QRCodeDecodeResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(repository)
            .decodeQRCode(captorRequest.capture())

        presenter.onValidateQRCode(QRCODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessValidateQRCode(captorResponse.capture())

        verify(view, never()).showError(any())

        assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `when calling onValidateQRCode and the return is error, it shows showError`() {
        val captorRequest = argumentCaptor<QRCodeDecodeRequest>()
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

        val errorObservable = Observable.error<RetrofitException>(exception)
        doReturn(errorObservable).whenever(repository)
            .decodeQRCode(captorRequest.capture())

        presenter.onValidateQRCode(QRCODE_MOCK)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captorError.capture())

        verify(view, never()).onSuccessValidateQRCode(any())

        assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        assertEquals(500, captorError.firstValue.httpStatus)
    }
}