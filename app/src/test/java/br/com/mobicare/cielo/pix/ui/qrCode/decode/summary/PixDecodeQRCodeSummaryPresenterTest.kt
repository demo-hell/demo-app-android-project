package br.com.mobicare.cielo.pix.ui.qrCode.decode.summary

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.PixTransferResponse
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeResponse
import br.com.mobicare.cielo.pix.domain.TransferRequest
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.QRCODE_MOCK
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

private const val OTP_MOCK = "0000"
private const val KEY_MOCK = "63538573069"
private const val CODE_MOCK = "eecc65f4-43f5-47d2-a5c9-dfd1537c049a"
private const val END_ID_MOCK = "6b68b5e675a7487ca5b98613b7c1895a"
private const val MESSAGE_MOCK = "teste cielo hml."
private const val DATE_MOCK = "2022-06-09"
private const val TRANSFER_END_ID_MOCK = "985064fb4e99441492309509d0efe8e5"

private const val AMOUNT_MOCK = 10.0

class PixDecodeQRCodeSummaryPresenterTest {

    private val transferResponse = "{\n" +
            "    \"idEndToEnd\": \"985064fb4e99441492309509d0efe8e5\",\n" +
            "    \"transactionDate\": \"2022-03-09T20:34:26.514\",\n" +
            "    \"transactionCode\": \"eecc65f4-43f5-47d2-a5c9-dfd1537c049a\",\n" +
            "    \"transactionStatus\": \"EXECUTED\"\n" +
            "}"

    private val transferResponsePending = "{\n" +
            "    \"idEndToEnd\": \"985064fb4e99441492309509d0efe8e5\",\n" +
            "    \"transactionDate\": \"2022-03-09T20:34:26.514\",\n" +
            "    \"transactionCode\": \"eecc65f4-43f5-47d2-a5c9-dfd1537c049a\",\n" +
            "    \"transactionStatus\": \"PENDING\"\n" +
            "}"

    private val transferResponseNotExecuted = "{\n" +
            "    \"idEndToEnd\": \"985064fb4e99441492309509d0efe8e5\",\n" +
            "    \"transactionDate\": \"2022-03-09T20:34:26.514\",\n" +
            "    \"transactionCode\": \"eecc65f4-43f5-47d2-a5c9-dfd1537c049a\",\n" +
            "    \"transactionStatus\": \"NOT_EXECUTED\"\n" +
            "}"

    private val decodeTransferResponse = "{\n" +
            "    \"endToEndId\":\"6b68b5e675a7487ca5b98613b7c1895a\",\n" +
            "    \"type\":\"STATIC\",\n" +
            "    \"pixType\":\"TRANSFER\",\n" +
            "    \"participant\":0,\n" +
            "    \"participantName\":\"CIELO S.A.\",\n" +
            "    \"receiverName\":\"MASSA DADOS AFIL. - 104-1\",\n" +
            "    \"receiverPersonType\":\"NATURAL_PERSON\",\n" +
            "    \"receiverDocument\":\"14407892072\",\n" +
            "    \"idTx\":\"IdTxteste00008527e7918048\",\n" +
            "    \"payerName\":\"teste\",\n" +
            "    \"payerDocument\":\"11111122211\",\n" +
            "    \"city\":\"São Paulo\",\n" +
            "    \"address\":\"Rua teste\",\n" +
            "    \"state\":\"SP\",\n" +
            "    \"zipCode\":\"11111111\",\n" +
            "    \"originalAmount\":3.0,\n" +
            "    \"finalAmount\":3.0,\n" +
            "    \"withDrawAmount\":0,\n" +
            "    \"changeAmount\":0,\n" +
            "    \"reusable\":true,\n" +
            "    \"branch\":\"1\",\n" +
            "    \"accountType\":\"3\",\n" +
            "    \"accountNumber\":\"46248000017\",\n" +
            "    \"key\":\"63538573069\",\n" +
            "    \"keyType\":\"CPF\",\n" +
            "    \"category\":\"0000\",\n" +
            "    \"payerType\":\"NATURAL_PERSON\",\n" +
            "    \"modalityAlteration\":\"NOT_ALLOWED\",\n" +
            "    \"ispbWithDraw\":\"1027058\",\n" +
            "    \"modalityAltWithDraw\":\"NOT_ALLOWED\",\n" +
            "    \"modalityWithDrawAgent\":\"AGTEC\",\n" +
            "    \"ispbChange\":\"1027058\",\n" +
            "    \"modalityAltChange\":\"NOT_ALLOWED\",\n" +
            "    \"modalityChangeAgent\":\"AGTEC\",\n" +
            "    \"status\":0\n" +
            "}"

    private val decodeChangeResponse = "{\n" +
            "    \"endToEndId\":\"6b68b5e675a7487ca5b98613b7c1895a\",\n" +
            "    \"type\":\"DYNAMIC\",\n" +
            "    \"pixType\":\"CHANGE\",\n" +
            "    \"participant\":0,\n" +
            "    \"participantName\":\"CIELO S.A.\",\n" +
            "    \"receiverName\":\"MASSA DADOS AFIL. - 104-1\",\n" +
            "    \"receiverPersonType\":\"NATURAL_PERSON\",\n" +
            "    \"receiverDocument\":\"14407892072\",\n" +
            "    \"idTx\":\"IdTxteste00008527e7918048\",\n" +
            "    \"payerName\":\"teste\",\n" +
            "    \"payerDocument\":\"11111122211\",\n" +
            "    \"city\":\"São Paulo\",\n" +
            "    \"address\":\"Rua teste\",\n" +
            "    \"state\":\"SP\",\n" +
            "    \"zipCode\":\"11111111\",\n" +
            "    \"originalAmount\":40.0,\n" +
            "    \"finalAmount\":40.0,\n" +
            "    \"withDrawAmount\":0,\n" +
            "    \"changeAmount\":30.0,\n" +
            "    \"reusable\":true,\n" +
            "    \"branch\":\"1\",\n" +
            "    \"accountType\":\"3\",\n" +
            "    \"accountNumber\":\"46248000017\",\n" +
            "    \"key\":\"63538573069\",\n" +
            "    \"keyType\":\"CPF\",\n" +
            "    \"category\":\"0000\",\n" +
            "    \"payerType\":\"NATURAL_PERSON\",\n" +
            "    \"modalityAlteration\":\"NOT_ALLOWED\",\n" +
            "    \"ispbWithDraw\":\"1027058\",\n" +
            "    \"modalityAltWithDraw\":\"NOT_ALLOWED\",\n" +
            "    \"modalityWithDrawAgent\":\"AGTEC\",\n" +
            "    \"ispbChange\":\"1027058\",\n" +
            "    \"modalityAltChange\":\"NOT_ALLOWED\",\n" +
            "    \"modalityChangeAgent\":\"AGTEC\",\n" +
            "    \"status\":0\n" +
            "}"

    private val decodeWithdrawalResponse = "{\n" +
            "    \"endToEndId\":\"6b68b5e675a7487ca5b98613b7c1895a\",\n" +
            "    \"type\":\"STATIC\",\n" +
            "    \"pixType\":\"WITHDRAWAL\",\n" +
            "    \"participant\":0,\n" +
            "    \"participantName\":\"CIELO S.A.\",\n" +
            "    \"receiverName\":\"MASSA DADOS AFIL. - 104-1\",\n" +
            "    \"receiverPersonType\":\"NATURAL_PERSON\",\n" +
            "    \"receiverDocument\":\"14407892072\",\n" +
            "    \"idTx\":\"IdTxteste00008527e7918048\",\n" +
            "    \"payerName\":\"teste\",\n" +
            "    \"payerDocument\":\"11111122211\",\n" +
            "    \"city\":\"São Paulo\",\n" +
            "    \"address\":\"Rua teste\",\n" +
            "    \"state\":\"SP\",\n" +
            "    \"zipCode\":\"11111111\",\n" +
            "    \"originalAmount\":10,\n" +
            "    \"finalAmount\":10,\n" +
            "    \"withDrawAmount\":10,\n" +
            "    \"changeAmount\":0,\n" +
            "    \"reusable\":true,\n" +
            "    \"branch\":\"1\",\n" +
            "    \"accountType\":\"3\",\n" +
            "    \"accountNumber\":\"46248000017\",\n" +
            "    \"key\":\"63538573069\",\n" +
            "    \"keyType\":\"CPF\",\n" +
            "    \"category\":\"0000\",\n" +
            "    \"payerType\":\"NATURAL_PERSON\",\n" +
            "    \"modalityAlteration\":\"NOT_ALLOWED\",\n" +
            "    \"ispbWithDraw\":\"1027058\",\n" +
            "    \"modalityAltWithDraw\":\"NOT_ALLOWED\",\n" +
            "    \"modalityWithDrawAgent\":\"AGTEC\",\n" +
            "    \"ispbChange\":\"1027058\",\n" +
            "    \"modalityAltChange\":\"NOT_ALLOWED\",\n" +
            "    \"modalityChangeAgent\":\"AGTEC\",\n" +
            "    \"status\":0\n" +
            "}"


    private val decodeResponse = "{\n" +
            "    \"endToEndId\":\"6b68b5e675a7487ca5b98613b7c1895a\",\n" +
            "    \"type\":\"DYNAMIC_COBV\",\n" +
            "    \"pixType\":\"TRANSFER\",\n" +
            "    \"participant\":1027058,\n" +
            "    \"participantName\":\"CIELO S.A.\",\n" +
            "    \"receiverName\":\"MASSA DADOS AFIL. - 104-1\",\n" +
            "    \"receiverTradingName\":\"string\",\n" +
            "    \"receiverPersonType\":\"NATURAL_PERSON\",\n" +
            "    \"receiverDocument\":\"14407892072\",\n" +
            "    \"idTx\":\"IdTxteste00008527e7918048\",\n" +
            "    \"payerName\":\"teste\",\n" +
            "    \"payerDocument\":\"11111122211\",\n" +
            "    \"city\":\"São Paulo\",\n" +
            "    \"address\":\"Rua teste\",\n" +
            "    \"state\":\"SP\",\n" +
            "    \"zipCode\":\"11111111\",\n" +
            "    \"originalAmount\":10.0,\n" +
            "    \"interest\":2,\n" +
            "    \"penalty\":1,\n" +
            "    \"discount\":0,\n" +
            "    \"abatement\":0,\n" +
            "    \"finalAmount\":10.0,\n" +
            "    \"withDrawAmount\":10.0,\n" +
            "    \"changeAmount\":0,\n" +
            "    \"reusable\":true,\n" +
            "    \"branch\":\"1\",\n" +
            "    \"accountType\":\"3\",\n" +
            "    \"accountNumber\":\"46248000017\",\n" +
            "    \"key\":\"63538573069\",\n" +
            "    \"keyType\":\"CPF\",\n" +
            "    \"category\":\"0000\",\n" +
            "    \"payerType\":\"NATURAL_PERSON\",\n" +
            "    \"modalityAlteration\":\"NOT_ALLOWED\",\n" +
            "    \"ispbWithDraw\":\"1027058\",\n" +
            "    \"modalityAltWithDraw\":\"NOT_ALLOWED\",\n" +
            "    \"modalityWithDrawAgent\":\"AGTEC\",\n" +
            "    \"ispbChange\":\"1027058\",\n" +
            "    \"modalityAltChange\":\"NOT_ALLOWED\",\n" +
            "    \"modalityChangeAgent\":\"AGTEC\",\n" +
            "    \"status\":0\n" +
            "}"

    @Mock
    lateinit var view: PixDecodeQRCodeSummaryContract.View

    @Mock
    lateinit var repository: PixTransferRepositoryContract

    @Mock
    lateinit var qrCodeRepository: PixQRCodeRepositoryContract

    @Mock
    lateinit var userPreferences: UserPreferences

    private lateinit var presenter: PixDecodeQRCodeSummaryPresenter

    private lateinit var decode: QRCodeDecodeResponse
    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        decode = decodeResponse()
        presenter = spy(
            PixDecodeQRCodeSummaryPresenter(
                view,
                userPreferences,
                repository,
                qrCodeRepository,
                uiScheduler,
                ioScheduler
            )
        )
    }

    @Test
    fun `When getUsername is called and value is empty, the return must be empty`() {
        doReturn("").whenever(userPreferences).userName
        assertEquals("", presenter.getUsername())
    }

    @Test
    fun `When getUsername is called and value is testeCielo, the return must be testeCielo`() {
        doReturn("testeCielo").whenever(userPreferences).userName
        assertEquals("testeCielo", presenter.getUsername())
    }

    @Test
    fun `when calling onPayQRCode, get a success return on POST transfer and transactionStatus == PENDING, show onTransactionInProcess`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val transferResponse = transferResponse(transferResponsePending)

        val transferSuccess = Observable.just(transferResponse)
        doReturn(transferSuccess).whenever(repository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccessPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            decode,
            EMPTY
        )


        verify(view).onSuccessPayQRCode(any())
        verify(view).onTransactionInProcess()

        verify(view, never()).onErrorPayQRCode(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPaymentError()
        verify(view, never()).onSuccessfulPayment(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(MESSAGE_MOCK, requestCaptor.firstValue.message)
        assertEquals(AMOUNT_MOCK, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_ESTATICO.name,
            requestCaptor.firstValue.transferType
        )

        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)
        assertEquals(null, requestCaptor.firstValue.agentMode)
        assertEquals(null, requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals(null, requestCaptor.firstValue.changeAmount)
        assertEquals(null, requestCaptor.firstValue.purchaseAmount)
    }

    @Test
    fun `when calling onPayQRCode, get a success return on POST transfer and transactionStatus == EXECUTED, show onSuccessfulPayment()`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()
        val captorTransfer = argumentCaptor<PixTransferResponse>()

        val transferResponse = transferResponse()

        val transferSuccess = Observable.just(transferResponse)
        doReturn(transferSuccess).whenever(repository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccessPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            decode,
            EMPTY
        )

        verify(view).onSuccessPayQRCode(any())
        verify(view).onSuccessfulPayment(captorTransfer.capture())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onErrorPayQRCode(any())

        verify(view, never()).showError(any())
        verify(view, never()).onPaymentError()

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(MESSAGE_MOCK, requestCaptor.firstValue.message)
        assertEquals(AMOUNT_MOCK, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_ESTATICO.name,
            requestCaptor.firstValue.transferType
        )

        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)
        assertEquals(null, requestCaptor.firstValue.agentMode)
        assertEquals(null, requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals(null, requestCaptor.firstValue.changeAmount)
        assertEquals(null, requestCaptor.firstValue.purchaseAmount)

        assertEquals(TRANSFER_END_ID_MOCK, captorTransfer.firstValue.idEndToEnd)
        assertEquals("2022-03-09T20:34:26.514", captorTransfer.firstValue.transactionDate)
        assertEquals("EXECUTED", captorTransfer.firstValue.transactionStatus)
        assertEquals(CODE_MOCK, captorTransfer.firstValue.transactionCode)
    }

    @Test
    fun `when calling onPayQRCode, get a success return on POST transfer and transactionStatus == NOT_EXECUTED, show onPaymentError`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val transferResponse = transferResponse(transferResponseNotExecuted)

        val transferSuccess = Observable.just(transferResponse)
        doReturn(transferSuccess).whenever(repository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccessPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.onPayQRCode(
            OTP_MOCK,
            null,
            null,
            AMOUNT_MOCK,
            decode,
            EMPTY
        )


        verify(view).onSuccessPayQRCode(any())
        verify(view).onPaymentError()

        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onErrorPayQRCode(any())

        verify(view, never()).showError(any())
        verify(view, never()).onSuccessfulPayment(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(AMOUNT_MOCK, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_ESTATICO.name,
            requestCaptor.firstValue.transferType
        )

        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)
        assertEquals(null, requestCaptor.firstValue.agentMode)
        assertEquals(null, requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals(null, requestCaptor.firstValue.changeAmount)
        assertEquals(null, requestCaptor.firstValue.purchaseAmount)
    }

    @Test
    fun `when calling onPayQRCode and get an error return on POST transfer show onErrorPayQRCode`() {
        val captor = argumentCaptor<ErrorMessage>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

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
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onErrorPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            decode,
            EMPTY
        )


        verify(view).onErrorPayQRCode(any())
        verify(view).showError(captor.capture())

        verify(view, never()).onSuccessPayQRCode(any())
        verify(view, never()).onPaymentError()
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onSuccessfulPayment(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(MESSAGE_MOCK, requestCaptor.firstValue.message)
        assertEquals(AMOUNT_MOCK, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_ESTATICO.name,
            requestCaptor.firstValue.transferType
        )

        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)
        assertEquals(null, requestCaptor.firstValue.agentMode)
        assertEquals(null, requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals(null, requestCaptor.firstValue.changeAmount)
        assertEquals(null, requestCaptor.firstValue.purchaseAmount)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when calling onPyQRCode when the pixType is CHANGE and transactionStatus == PENDING, show onTransactionInProcess`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val transferResponse = transferResponse(transferResponsePending)

        val transferSuccess = Observable.just(transferResponse)
        doReturn(transferSuccess).whenever(repository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccessPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            decodeResponse(decodeChangeResponse),
            EMPTY
        )


        verify(view).onSuccessPayQRCode(any())
        verify(view).onTransactionInProcess()

        verify(view, never()).onErrorPayQRCode(any())

        verify(view, never()).showError(any())
        verify(view, never()).onPaymentError()
        verify(view, never()).onSuccessfulPayment(any())

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(MESSAGE_MOCK, requestCaptor.firstValue.message)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_DINAMICO.name,
            requestCaptor.firstValue.transferType
        )
        assertEquals(PixQRCodeOperationTypeEnum.CHANGE.name, requestCaptor.firstValue.pixType)
        assertEquals(50.0, requestCaptor.firstValue.amount)
        assertEquals("AGTEC", requestCaptor.firstValue.agentMode)
        assertEquals("1027058", requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals("10.0", requestCaptor.firstValue.changeAmount)

        //TODO verify
//        assertEquals("10.0", requestCaptor.firstValue.purchaseAmount)
    }

    @Test
    fun `when calling onPyQRCode when the pixType is WITHDRAWAL and transactionStatus == EXECUTED, show onSuccessfulPayment()`() {
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()
        val captorTransfer = argumentCaptor<PixTransferResponse>()

        val transferResponse = transferResponse()

        val transferSuccess = Observable.just(transferResponse)
        doReturn(transferSuccess).whenever(repository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        `when`(view.onSuccessPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }
        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            decodeResponse(decodeWithdrawalResponse),
            EMPTY
        )

        verify(view).onSuccessPayQRCode(any())
        verify(view).onSuccessfulPayment(captorTransfer.capture())

        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onErrorPayQRCode(any())

        verify(view, never()).showError(any())
        verify(view, never()).onPaymentError()

        assertEquals(OTP_MOCK, otpCaptor.firstValue)
        assertEquals(MESSAGE_MOCK, requestCaptor.firstValue.message)
        assertEquals(AMOUNT_MOCK, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals(KEY_MOCK, requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(
            PixTransferTypeEnum.QR_CODE_ESTATICO.name,
            requestCaptor.firstValue.transferType
        )

        assertEquals("AGTEC", requestCaptor.firstValue.agentMode)
        assertEquals("1027058", requestCaptor.firstValue.agentWithdrawalIspb)
        assertEquals(PixQRCodeOperationTypeEnum.WITHDRAWAL.name, requestCaptor.firstValue.pixType)
        assertEquals(null, requestCaptor.firstValue.changeAmount)
        assertEquals(null, requestCaptor.firstValue.purchaseAmount)

        assertEquals(TRANSFER_END_ID_MOCK, captorTransfer.firstValue.idEndToEnd)
        assertEquals("2022-03-09T20:34:26.514", captorTransfer.firstValue.transactionDate)
        assertEquals("EXECUTED", captorTransfer.firstValue.transactionStatus)
        assertEquals(CODE_MOCK, captorTransfer.firstValue.transactionCode)
    }

    @Test
    fun `when calling onDecode and QRCodeDecodeResponse is null, show onPaymentError`() {
        val captorScheduling = argumentCaptor<String>()
        val captorRequest = argumentCaptor<QRCodeDecodeRequest>()
        val captorDecode = argumentCaptor<QRCodeDecodeResponse>()

        val decodeResponse = decodeResponse(decodeResponse)

        doReturn(Observable.just(decodeResponse)).whenever(qrCodeRepository)
            .decodeQRCode(captorRequest.capture())

        presenter.onDecode(
            qrcode = QRCODE_MOCK,
            scheduling = DATE_MOCK
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onSuccessDecode(captorDecode.capture(), captorScheduling.capture())


        verify(view, never()).onErrorDecode(any())
        verify(view, never()).onSuccessScheduling(any())

        assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        assertEquals(DATE_MOCK, captorRequest.firstValue.paymentDateIntended)

        assertEquals(DATE_MOCK, captorScheduling.firstValue)
        assertTrue(captorDecode.allValues.contains(decodeResponse))
    }

    @Test
    fun `when calling onDecode and QRCodeDecodeResponse is null, show showError`() {
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

        doReturn(Observable.error<RetrofitException>(exception)).whenever(qrCodeRepository)
            .decodeQRCode(captorRequest.capture())

        presenter.onDecode(
            qrcode = QRCODE_MOCK,
            scheduling = DATE_MOCK
        )

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onErrorDecode(captorError.capture())


        verify(view, never()).onSuccessDecode(any(), any())
        verify(view, never()).onSuccessScheduling(any())

        assertEquals(QRCODE_MOCK, captorRequest.firstValue.qrCode)
        assertEquals(DATE_MOCK, captorRequest.firstValue.paymentDateIntended)

        assertEquals(500, captorError.firstValue.httpStatus)
    }

    @Test
    fun `when calling onPayQRCode and QRCodeDecodeResponse is null, show onPaymentError`() {
        `when`(view.onErrorPayQRCode(any())).then { invocation ->
            (invocation.arguments[0] as () -> Unit).invoke()
        }

        presenter.onPayQRCode(
            OTP_MOCK,
            MESSAGE_MOCK,
            null,
            AMOUNT_MOCK,
            null,
            EMPTY
        )

        verify(view).onErrorPayQRCode(any())
        verify(view).showError()


        verify(view, never()).onSuccessPayQRCode(any())
        verify(view, never()).onPaymentError()
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onSuccessfulPayment(any())
    }

    @Test
    fun `when calling getValuePaymentOrChange and pixType is WITHDRAWAL use the withDrawAmount field`() {
        val value = presenter.getTransferValue(decodeResponse(decodeWithdrawalResponse))
        assertEquals(10.0, value, Double.NaN)
    }

    @Test
    fun `when calling getValuePaymentOrChange and pixType is TRANSFER use the finalAmount field`() {
        val response = decodeResponse(decodeTransferResponse)
        val value = presenter.getTransferValue(response)

        assertEquals(3.0, value, Double.NaN)
    }

    @Test
    fun `when calling getValuePaymentOrChange and pixType is CHANGE use the changeAmount field`() {
        val value = presenter.getTransferValue(decodeResponse(decodeChangeResponse))
        assertEquals(30.0, value, Double.NaN)
    }

    private fun transferResponse(response: String = transferResponse): PixTransferResponse =
        Gson().fromJson(response, PixTransferResponse::class.java)

    private fun decodeResponse(response: String = decodeTransferResponse): QRCodeDecodeResponse =
        Gson().fromJson(response, QRCodeDecodeResponse::class.java)
}