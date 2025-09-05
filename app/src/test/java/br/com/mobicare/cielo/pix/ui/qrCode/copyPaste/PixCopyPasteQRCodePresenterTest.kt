package br.com.mobicare.cielo.pix.ui.qrCode.copyPaste

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste.PixCopyPasteQRCodeContract
import br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste.PixCopyPasteQRCodePresenter
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.QRCODE_MOCK
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PixCopyPasteQRCodePresenterTest {

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
    lateinit var view: PixCopyPasteQRCodeContract.View

    @Mock
    lateinit var repository: PixQRCodeRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixCopyPasteQRCodePresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixCopyPasteQRCodePresenter(
            view,
            userPreferences,
            repository,
            uiScheduler,
            ioScheduler
        )
    }

    @Test
    fun `when calling PixCopyPasteQRCodePresenter_onValidateQRCode and the return is success, it shows PixCopyPasteQRCodePresenter_onSuccessValidateQRCode`() {
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

        Assert.assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        Assert.assertTrue(captorResponse.allValues.contains(response))
    }

    @Test
    fun `when calling PixCopyPasteQRCodePresenter_onValidateQRCode and the return is error, it shows PixCopyPasteQRCodePresenter_showError`() {
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

        Assert.assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        Assert.assertEquals(500, captorError.firstValue.httpStatus)
    }
}