package br.com.mobicare.cielo.pix.ui.transfer.summary

import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.domain.PixManualTransferRequest
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.extract.PixExtractRepositoryContract
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import com.google.gson.Gson
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val AMOUNT = 30.0
private const val OTP = "0000"
private const val END_ID_MOCK = "6b68b5e675a7487ca5b98613b7c1895a"
private const val CODE_MOCK = "eecc65f4-43f5-47d2-a5c9-dfd1537c049a"
private const val TRANSFER_END_ID_MOCK = "985064fb4e99441492309509d0efe8e5"

class PixTransferSummaryPresenterTest {

    private val validateKey = "{\n" +
            "\"key\": \"63538573069\",\n" +
            "\"keyType\": \"CPF\",\n" +
            "\"participant\": \"1027058\",\n" +
            "\"participantName\": \"Vazio\",\n" +
            "\"branch\": \"0001\",\n" +
            "\"accountType\": \"PYMT\",\n" +
            "\"accountNumber\": \"2692100001\",\n" +
            "\"ownerType\": \"NATURAL_PERSON\",\n" +
            "\"ownerName\": \"Chapeuzinho Do Nascimento\",\n" +
            "\"ownerDocument\": \"***.828.261-**\",\n" +
            "\"creationDate\": \"2022-02-11T14:55:16.261Z\",\n" +
            "\"ownershipDate\": \"2022-02-11T14:55:16.258Z\",\n" +
            "\"claimType\": \"POSSESSION_CLAIM\",\n" +
            "\"endToEndId\": \"6b68b5e675a7487ca5b98613b7c1895a\"\n" +
            "}"

    private val response = "{\n" +
            "    \"idEndToEnd\": \"985064fb4e99441492309509d0efe8e5\",\n" +
            "    \"transactionDate\": \"2022-03-09T20:34:26.514\",\n" +
            "    \"transactionCode\": \"eecc65f4-43f5-47d2-a5c9-dfd1537c049a\",\n" +
            "    \"transactionStatus\": \"PENDING\"\n" +
            "}"

    private val detailsPendingResponse = "{\n" +
            "    \"idAccount\": \"3082\",\n" +
            "    \"transactionType\": \"TRANSFER_DEBIT\",\n" +
            "    \"idEndToEnd\": \"a5b360b9a9f64da69006f01a7c57d6cb\",\n" +
            "    \"transferType\": \"1\",\n" +
            "    \"transactionStatus\": \"PENDING\",\n" +
            "    \"debitParty\": {\n" +
            "        \"ispb\": \"1027058\",\n" +
            "        \"name\": \"Ruth Rocha\",\n" +
            "        \"bankName\": \"CIELO S.A.\",\n" +
            "        \"bankAccountNumber\": \"58688000019\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"bankAccountType\": \"CC\",\n" +
            "        \"nationalRegistration\": \"90388620838\"\n" +
            "    },\n" +
            "    \"errorType\": \"20\",\n" +
            "    \"errorCode\": \"ED05\",\n" +
            "    \"errorMessage\": \"Erro no processamento do pagamento (erro genérico)\",\n" +
            "    \"amount\": 0.01,\n" +
            "    \"creditParty\": {\n" +
            "        \"ispb\": \"30723871\",\n" +
            "        \"bankName\": \"BANQI INSTITUICAO DE PAGAMENTO LTDA. \",\n" +
            "        \"name\": \"Maria da Silva Santos\",\n" +
            "        \"bankAccountType\": \"PA\",\n" +
            "        \"nationalRegistration\": \"83917818019\",\n" +
            "        \"bankAccountNumber\": \"1271424889\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"key\": \"83917818019\"\n" +
            "    },\n" +
            "    \"finalAmount\": \"0.01\",\n" +
            "    \"tariffAmount\": \"0\",\n" +
            "    \"idAdjustment\": \"61674\",\n" +
            "    \"payerAnswer\": \"Teste\",\n" +
            "    \"transactionCode\": \"4a2acd2f-73c0-4089-842b-14256972d1d9\",\n" +
            "    \"transactionDate\": \"2022-03-08T19:10:52.659\"\n" +
            "}"


    private val detailsExecutedResponse = "{\n" +
            "    \"idAccount\": \"3082\",\n" +
            "    \"transactionType\": \"TRANSFER_DEBIT\",\n" +
            "    \"idEndToEnd\": \"a5b360b9a9f64da69006f01a7c57d6cb\",\n" +
            "    \"transferType\": \"1\",\n" +
            "    \"transactionStatus\": \"EXECUTED\",\n" +
            "    \"debitParty\": {\n" +
            "        \"ispb\": \"1027058\",\n" +
            "        \"name\": \"Ruth Rocha\",\n" +
            "        \"bankName\": \"CIELO S.A.\",\n" +
            "        \"bankAccountNumber\": \"58688000019\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"bankAccountType\": \"CC\",\n" +
            "        \"nationalRegistration\": \"90388620838\"\n" +
            "    },\n" +
            "    \"errorType\": \"20\",\n" +
            "    \"errorCode\": \"ED05\",\n" +
            "    \"errorMessage\": \"Erro no processamento do pagamento (erro genérico)\",\n" +
            "    \"amount\": 0.01,\n" +
            "    \"creditParty\": {\n" +
            "        \"ispb\": \"30723871\",\n" +
            "        \"bankName\": \"BANQI INSTITUICAO DE PAGAMENTO LTDA. \",\n" +
            "        \"name\": \"Maria da Silva Santos\",\n" +
            "        \"bankAccountType\": \"PA\",\n" +
            "        \"nationalRegistration\": \"83917818019\",\n" +
            "        \"bankAccountNumber\": \"1271424889\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"key\": \"83917818019\"\n" +
            "    },\n" +
            "    \"finalAmount\": \"0.01\",\n" +
            "    \"tariffAmount\": \"0\",\n" +
            "    \"idAdjustment\": \"61674\",\n" +
            "    \"payerAnswer\": \"Teste\",\n" +
            "    \"transactionCode\": \"4a2acd2f-73c0-4089-842b-14256972d1d9\",\n" +
            "    \"transactionDate\": \"2022-03-08T19:10:52.659\"\n" +
            "}"

    private val detailsErrorResponse = "{\n" +
            "    \"idAccount\": \"3082\",\n" +
            "    \"transactionType\": \"TRANSFER_DEBIT\",\n" +
            "    \"idEndToEnd\": \"a5b360b9a9f64da69006f01a7c57d6cb\",\n" +
            "    \"transferType\": \"1\",\n" +
            "    \"transactionStatus\": \"NOT_EXECUTED\",\n" +
            "    \"debitParty\": {\n" +
            "        \"ispb\": \"1027058\",\n" +
            "        \"name\": \"Ruth Rocha\",\n" +
            "        \"bankName\": \"CIELO S.A.\",\n" +
            "        \"bankAccountNumber\": \"58688000019\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"bankAccountType\": \"CC\",\n" +
            "        \"nationalRegistration\": \"90388620838\"\n" +
            "    },\n" +
            "    \"errorType\": \"20\",\n" +
            "    \"errorCode\": \"ED05\",\n" +
            "    \"errorMessage\": \"Erro no processamento do pagamento (erro genérico)\",\n" +
            "    \"amount\": 0.01,\n" +
            "    \"creditParty\": {\n" +
            "        \"ispb\": \"30723871\",\n" +
            "        \"bankName\": \"BANQI INSTITUICAO DE PAGAMENTO LTDA. \",\n" +
            "        \"name\": \"Maria da Silva Santos\",\n" +
            "        \"bankAccountType\": \"PA\",\n" +
            "        \"nationalRegistration\": \"83917818019\",\n" +
            "        \"bankAccountNumber\": \"1271424889\",\n" +
            "        \"bankBranchNumber\": \"0001\",\n" +
            "        \"key\": \"83917818019\"\n" +
            "    },\n" +
            "    \"finalAmount\": \"0.01\",\n" +
            "    \"tariffAmount\": \"0\",\n" +
            "    \"idAdjustment\": \"61674\",\n" +
            "    \"payerAnswer\": \"Teste\",\n" +
            "    \"transactionCode\": \"4a2acd2f-73c0-4089-842b-14256972d1d9\",\n" +
            "    \"transactionDate\": \"2022-03-08T19:10:52.659\"\n" +
            "}"

    private val pixManualTransferRequestJson = "{\n" +
            "   \"finalAmount\":1.3,\n" +
            "   \"payee\":{\n" +
            "      \"bankAccountNumber\":\"123456\",\n" +
            "      \"bankAccountType\":\"PA\",\n" +
            "      \"bankBranchNumber\":\"2770\",\n" +
            "      \"bankName\":\"Banco Bradescard S.A.\",\n" +
            "      \"beneficiaryType\":\"F\",\n" +
            "      \"documentNumber\":\"13568998888\",\n" +
            "      \"ispb\":4184779,\n" +
            "      \"name\":\"teste teste\"\n" +
            "   }\n" +
            "}"

    @Mock
    lateinit var view: PixTransferSummaryContract.View

    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var pixTransferRepository: PixTransferRepositoryContract

    @Mock
    lateinit var pixExtractRepository: PixExtractRepositoryContract

    private lateinit var presenter: PixTransferSummaryPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixTransferSummaryPresenter(
            view,
            userPreferences,
            pixTransferRepository,
            pixExtractRepository,
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
        doReturn("testeCielo").whenever(userPreferences).userName
        assertEquals("testeCielo", presenter.getUsername())
    }

    @Test
    fun `when calling onTransfer, get a success return on POST transfer and GET details and transactionStatus == PENDING, show onTransactionInProcess`() {
        val otpCaptor = argumentCaptor<String>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val detailsResponse =
            Gson().fromJson(detailsPendingResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, "teste", EMPTY, EMPTY)

        verify(view).onTransactionInProcess()
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(OTP, otpCaptor.firstValue)
        assertEquals("teste", requestCaptor.firstValue.message)
        assertEquals(AMOUNT, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals("63538573069", requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(PixTransferTypeEnum.CHAVE.name, requestCaptor.firstValue.transferType)
        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
    }


    @Test
    fun `get error when try do pix transfer at time less than 1 minute`() {

        val otpCaptor = argumentCaptor<String>()
        val pixTransferRequest = argumentCaptor<TransferRequest>()
        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)

        val errorMessage = ErrorMessage().apply {
            this.title = ""
            this.httpStatus = 420
            this.code = "420"
            this.errorCode = ERROR_CODE_TOO_MANY_REQUESTS
            this.errorMessage = "Você foi bloqueado por execeder o número máximo de transações em 1 minuto. Por favor aguarde 1 hora para tentar novamente"
        }
        val response = APIUtils.createResponse(errorMessage)

        val exception = RetrofitException(
            message =  "Você foi bloqueado por execder o número máximo de transações em 1 minuto. Por favor aguarde 1 hora para tentar novamente.",
            url = null,
            response = response,
            kind = RetrofitException.Kind.HTTP,
            exception = null,
            retrofit = null,
            httpStatus = 420
        )

        val errorObservable = Observable.error<RetrofitException>(exception)

        doReturn(errorObservable).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), pixTransferRequest.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, "teste", EMPTY, EMPTY)


        verify(view).onPixManyRequestsError(any())
        verify(view, never()).showError(any())
        verify(view, never()).showBottomSheetScheduledTransaction(any())
        verify(view, never()).showBottomSheetSuccessfulTransaction(any(), any())
    }

    @Test
    fun `when calling onTransfer, get a success return on POST transfer and GET details and transactionStatus == EXECUTED, show onShowSuccessTransfer`() {
        val otpCaptor = argumentCaptor<String>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()
        val captorTransfer = argumentCaptor<PixTransferResponse>()
        val captorDetails = argumentCaptor<TransferDetailsResponse>()

        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val detailsResponse =
            Gson().fromJson(detailsExecutedResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, null, EMPTY, EMPTY)

        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(OTP, otpCaptor.firstValue)
        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(AMOUNT, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals("63538573069", requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(PixTransferTypeEnum.CHAVE.name, requestCaptor.firstValue.transferType)
        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)

        assertEquals(CODE_MOCK, codeCaptor.firstValue)
    }

    @Test
    fun `when calling onTransfer, get a success return on POST transfer and GET details and transactionStatus == NOT_EXECUTED, show onError`() {
        val otpCaptor = argumentCaptor<String>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val errorCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val detailsResponse =
            Gson().fromJson(detailsErrorResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, null, EMPTY, EMPTY)

        verify(view).onError(errorCaptor.capture())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(OTP, otpCaptor.firstValue)
        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(AMOUNT, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals("63538573069", requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(PixTransferTypeEnum.CHAVE.name, requestCaptor.firstValue.transferType)
        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
        assertEquals("Erro no processamento do pagamento (erro genérico)", errorCaptor.firstValue)
    }

    @Test
    fun `when calling onTransfer and get an error return on POST transfer show showError`() {
        val captor = argumentCaptor<ErrorMessage>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)

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
        doReturn(errorObservable).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, "teste", EMPTY, EMPTY)

        verify(view).showError(captor.capture())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(OTP, otpCaptor.firstValue)
        assertEquals("teste", requestCaptor.firstValue.message)
        assertEquals(AMOUNT, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals("63538573069", requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(PixTransferTypeEnum.CHAVE.name, requestCaptor.firstValue.transferType)
        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when calling onTransfer, get a success return on POST transfer and error in GET details show onTransactionInProcess`() {
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<TransferRequest>()

        val validateKey = Gson().fromJson(validateKey, ValidateKeyResponse::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transfer(otpCaptor.capture(), requestCaptor.capture())

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
        doReturn(errorObservable).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onTransfer(OTP, validateKey, AMOUNT, "teste", EMPTY, EMPTY)

        verify(view).onTransactionInProcess()
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(OTP, otpCaptor.firstValue)
        assertEquals("teste", requestCaptor.firstValue.message)
        assertEquals(AMOUNT, requestCaptor.firstValue.amount)
        assertEquals(END_ID_MOCK, requestCaptor.firstValue.idEndToEnd)
        assertEquals("63538573069", requestCaptor.firstValue.payee?.key)
        assertEquals(PixKeyTypeEnum.CPF.name, requestCaptor.firstValue.payee?.keyType)
        assertEquals(PixTransferTypeEnum.CHAVE.name, requestCaptor.firstValue.transferType)
        assertEquals(PixQRCodeOperationTypeEnum.TRANSFER.name, requestCaptor.firstValue.pixType)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
    }

    @Test
    fun `when calling onTransfer when the ValidateKeyResponse is null will show showError`() {
        presenter.onTransfer(OTP, null, AMOUNT, "teste", EMPTY, EMPTY)
        verify(view).showError(anyOrNull())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onPixManyRequestsError(any())
    }

    @Test
    fun `when calling onBankTransfer, get a success return on POST transferToBankAccount and GET details and transactionStatus == PENDING, show onTransactionInProcess`() {
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<PixManualTransferRequest>()

        val manualTransferRequest =
            Gson().fromJson(pixManualTransferRequestJson, PixManualTransferRequest::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val detailsResponse =
            Gson().fromJson(detailsPendingResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transferToBankAccount(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onBankTransfer(OTP, manualTransferRequest)

        verify(view).onTransactionInProcess()
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
        assertEquals(OTP, otpCaptor.firstValue)

        assertEquals(1.3, requestCaptor.firstValue.finalAmount, Double.NaN)
        assertEquals("123456", requestCaptor.firstValue.payee?.bankAccountNumber)
        assertEquals("PA", requestCaptor.firstValue.payee?.bankAccountType)
        assertEquals("2770", requestCaptor.firstValue.payee?.bankBranchNumber)
        assertEquals("Banco Bradescard S.A.", requestCaptor.firstValue.payee?.bankName)
        assertEquals("F", requestCaptor.firstValue.payee?.beneficiaryType)
        assertEquals("13568998888", requestCaptor.firstValue.payee?.documentNumber)
        assertEquals(4184779, requestCaptor.firstValue.payee?.ispb)
        assertEquals("teste teste", requestCaptor.firstValue.payee?.name)
        assertEquals(null, requestCaptor.firstValue.message)
    }

    @Test
    fun `when calling onBankTransfer, get a success return on POST transferToBankAccount and GET details and transactionStatus == EXECUTED, show onShowSuccessTransfer`() {
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val otpCaptor = argumentCaptor<String>()
        val captorTransfer = argumentCaptor<PixTransferResponse>()
        val captorDetails = argumentCaptor<TransferDetailsResponse>()
        val requestCaptor = argumentCaptor<PixManualTransferRequest>()

        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val manualTransferRequest =
            Gson().fromJson(pixManualTransferRequestJson, PixManualTransferRequest::class.java)
        val detailsResponse =
            Gson().fromJson(detailsExecutedResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transferToBankAccount(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onBankTransfer(OTP, manualTransferRequest)

        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(1.3, requestCaptor.firstValue.finalAmount, Double.NaN)
        assertEquals("123456", requestCaptor.firstValue.payee?.bankAccountNumber)
        assertEquals("PA", requestCaptor.firstValue.payee?.bankAccountType)
        assertEquals("2770", requestCaptor.firstValue.payee?.bankBranchNumber)
        assertEquals("Banco Bradescard S.A.", requestCaptor.firstValue.payee?.bankName)
        assertEquals("F", requestCaptor.firstValue.payee?.beneficiaryType)
        assertEquals("13568998888", requestCaptor.firstValue.payee?.documentNumber)
        assertEquals(4184779, requestCaptor.firstValue.payee?.ispb)
        assertEquals("teste teste", requestCaptor.firstValue.payee?.name)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
        assertEquals(OTP, otpCaptor.firstValue)
    }

    @Test
    fun `when calling onBankTransfer, get a success return on POST transferToBankAccount and GET details and transactionStatus == NOT_EXECUTED, show onError`() {
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val otpCaptor = argumentCaptor<String>()
        val errorCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<PixManualTransferRequest>()

        val manualTransferRequest =
            Gson().fromJson(pixManualTransferRequestJson, PixManualTransferRequest::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)
        val detailsResponse =
            Gson().fromJson(detailsErrorResponse, TransferDetailsResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transferToBankAccount(otpCaptor.capture(), requestCaptor.capture())

        val successDetails = Observable.just(detailsResponse)
        doReturn(successDetails).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onBankTransfer(OTP, manualTransferRequest)

        verify(view).onError(errorCaptor.capture())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(1.3, requestCaptor.firstValue.finalAmount, Double.NaN)
        assertEquals("123456", requestCaptor.firstValue.payee?.bankAccountNumber)
        assertEquals("PA", requestCaptor.firstValue.payee?.bankAccountType)
        assertEquals("2770", requestCaptor.firstValue.payee?.bankBranchNumber)
        assertEquals("Banco Bradescard S.A.", requestCaptor.firstValue.payee?.bankName)
        assertEquals("F", requestCaptor.firstValue.payee?.beneficiaryType)
        assertEquals("13568998888", requestCaptor.firstValue.payee?.documentNumber)
        assertEquals(4184779, requestCaptor.firstValue.payee?.ispb)
        assertEquals("teste teste", requestCaptor.firstValue.payee?.name)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
        assertEquals(OTP, otpCaptor.firstValue)

        assertEquals("Erro no processamento do pagamento (erro genérico)", errorCaptor.firstValue)
    }

    @Test
    fun `when calling onBankTransfer and get an error return on POST transferToBankAccount show showError`() {
        val captor = argumentCaptor<ErrorMessage>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<PixManualTransferRequest>()

        val manualTransferRequest =
            Gson().fromJson(pixManualTransferRequestJson, PixManualTransferRequest::class.java)

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
        doReturn(errorObservable).whenever(pixTransferRepository)
            .transferToBankAccount(otpCaptor.capture(), requestCaptor.capture())

        presenter.onBankTransfer(OTP, manualTransferRequest)

        verify(view).showError(captor.capture())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(1.3, requestCaptor.firstValue.finalAmount, Double.NaN)
        assertEquals("123456", requestCaptor.firstValue.payee?.bankAccountNumber)
        assertEquals("PA", requestCaptor.firstValue.payee?.bankAccountType)
        assertEquals("2770", requestCaptor.firstValue.payee?.bankBranchNumber)
        assertEquals("Banco Bradescard S.A.", requestCaptor.firstValue.payee?.bankName)
        assertEquals("F", requestCaptor.firstValue.payee?.beneficiaryType)
        assertEquals("13568998888", requestCaptor.firstValue.payee?.documentNumber)
        assertEquals(4184779, requestCaptor.firstValue.payee?.ispb)
        assertEquals("teste teste", requestCaptor.firstValue.payee?.name)

        assertEquals(OTP, otpCaptor.firstValue)

        assertEquals(500, captor.firstValue.httpStatus)
    }

    @Test
    fun `when calling onBankTransfer, get a success return on POST transferToBankAccount and error in GET details show onTransactionInProcess`() {
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()
        val otpCaptor = argumentCaptor<String>()
        val requestCaptor = argumentCaptor<PixManualTransferRequest>()

        val manualTransferRequest =
            Gson().fromJson(pixManualTransferRequestJson, PixManualTransferRequest::class.java)
        val response = Gson().fromJson(response, PixTransferResponse::class.java)

        val success = Observable.just(response)
        doReturn(success).whenever(pixTransferRepository)
            .transferToBankAccount(otpCaptor.capture(), requestCaptor.capture())

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
        doReturn(errorObservable).whenever(pixTransferRepository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onBankTransfer(OTP, manualTransferRequest)

        verify(view).onTransactionInProcess()
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onError(any())
        verify(view, never()).showError(any())
        verify(view, never()).onPixManyRequestsError(any())

        assertEquals(null, requestCaptor.firstValue.message)
        assertEquals(1.3, requestCaptor.firstValue.finalAmount, Double.NaN)
        assertEquals("123456", requestCaptor.firstValue.payee?.bankAccountNumber)
        assertEquals("PA", requestCaptor.firstValue.payee?.bankAccountType)
        assertEquals("2770", requestCaptor.firstValue.payee?.bankBranchNumber)
        assertEquals("Banco Bradescard S.A.", requestCaptor.firstValue.payee?.bankName)
        assertEquals("F", requestCaptor.firstValue.payee?.beneficiaryType)
        assertEquals("13568998888", requestCaptor.firstValue.payee?.documentNumber)
        assertEquals(4184779, requestCaptor.firstValue.payee?.ispb)
        assertEquals("teste teste", requestCaptor.firstValue.payee?.name)

        assertEquals(TRANSFER_END_ID_MOCK, endIdCaptor.firstValue)
        assertEquals(CODE_MOCK, codeCaptor.firstValue)
        assertEquals(OTP, otpCaptor.firstValue)
    }

    @Test
    fun `when calling onBankTransfer when the PixManualTransferRequest is null will show showError`() {
        presenter.onBankTransfer(OTP, null)

        verify(view).showError(anyOrNull())
        verify(view, never()).onSuccessFlow(any())
        verify(view, never()).onTransactionInProcess()
        verify(view, never()).onPixManyRequestsError(any())
    }
}