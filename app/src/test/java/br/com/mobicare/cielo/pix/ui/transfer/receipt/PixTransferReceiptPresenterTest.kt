package br.com.mobicare.cielo.pix.ui.transfer.receipt

import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
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

private const val END_ID = "985064fb4e99441492309509d0efe8e5"
private const val CODE = "eecc65f4-43f5-47d2-a5c9-dfd1537c049a"

class PixTransferReceiptPresenterTest {

    private val pixTransfer = "{\n" +
            "    \"idEndToEnd\": \"985064fb4e99441492309509d0efe8e5\",\n" +
            "    \"transactionDate\": \"2022-03-09T20:34:26.514\",\n" +
            "    \"transactionCode\": \"eecc65f4-43f5-47d2-a5c9-dfd1537c049a\",\n" +
            "    \"transactionStatus\": \"PENDING\"\n" +
            "}"

    private val keyTransferResponse = "{\n" +
            "  \"idAccountType\": \"string\",\n" +
            "  \"idAccount\": \"string\",\n" +
            "  \"transactionType\": \"string\",\n" +
            "  \"idEndToEnd\": \"string\",\n" +
            "  \"transferType\": 1,\n" +
            "  \"transactionStatus\": \"string\",\n" +
            "  \"debitParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"errorType\": \"string\",\n" +
            "  \"errorCode\": \"string\",\n" +
            "  \"errorMessage\": \"string\",\n" +
            "  \"amount\": 0,\n" +
            "  \"creditParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"finalAmount\": 0,\n" +
            "  \"tariffAmount\": 0,\n" +
            "  \"idAdjustment\": \"string\",\n" +
            "  \"payerAnswer\": \"string\",\n" +
            "  \"transactionCode\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
            "  \"transactionDate\": \"2022-04-28T17:19:18.570Z\",\n" +
            "  \"pixType\": \"WITHDRAWAL\",\n" +
            "  \"purchaseAmount\": 0,\n" +
            "  \"changeAmount\": 0,\n" +
            "  \"agentWithdrawalIspb\": \"string\",\n" +
            "  \"agentMode\": \"AGPSS\"\n" +
            "}"

    private val manualResponse = "{\n" +
            "    \"transactionType\":\"TRANSFER_DEBIT\",\n" +
            "    \"idEndToEnd\":\"E087448172022042519434F4P3DSQJGP\",\n" +
            "    \"transferType\":\"0\",\n" +
            "    \"transactionStatus\":\"NOT_EXECUTED\",\n" +
            "    \"debitParty\":{\n" +
            "        \"ispb\":\"8744817\",\n" +
            "        \"name\":\"MASSA DADOS AFIL. - 341-85599\",\n" +
            "        \"bankName\":\"DOCK SOLUCOES EM MEIOS DE PAGAMENTO S A\",\n" +
            "        \"bankAccountNumber\":\"30941200017\",\n" +
            "        \"bankBranchNumber\":\"0001\",\n" +
            "        \"bankAccountType\":\"CC\",\n" +
            "        \"nationalRegistration\":\"66691598000110\"\n" +
            "    },\n" +
            "    \"errorType\":\"20\",\n" +
            "    \"errorCode\":\"ED05\",\n" +
            "    \"errorMessage\":\"Erro no processamento do pagamento (erro genérico)\",\n" +
            "    \"amount\":0.01,\n" +
            "    \"creditParty\":{\n" +
            "        \"ispb\":\"4184779\",\n" +
            "        \"name\":\"teste\",\n" +
            "        \"bankAccountType\":\"CC\",\n" +
            "        \"nationalRegistration\":\"***85990000***\",\n" +
            "        \"bankAccountNumber\":\"46248000017\",\n" +
            "        \"bankBranchNumber\":\"0001\"\n" +
            "    },\n" +
            "    \"finalAmount\":\"0.01\",\n" +
            "    \"tariffAmount\":\"0\",\n" +
            "    \"idAdjustment\":\"68690\",\n" +
            "    \"transactionCode\":\"967c1c73-c1b3-4690-91b3-d1abbafa214d\",\n" +
            "    \"transactionDate\":\"2022-04-25T19:43:53.769\"\n" +
            "}"

    private val withdrawQrCodeResponse = "{\n" +
            "  \"idAccountType\": \"string\",\n" +
            "  \"idAccount\": \"string\",\n" +
            "  \"transactionType\": \"string\",\n" +
            "  \"idEndToEnd\": \"string\",\n" +
            "  \"transferType\": 2,\n" +
            "  \"transactionStatus\": \"string\",\n" +
            "  \"debitParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"errorType\": \"string\",\n" +
            "  \"errorCode\": \"string\",\n" +
            "  \"errorMessage\": \"string\",\n" +
            "  \"amount\": 0,\n" +
            "  \"creditParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"finalAmount\": 0,\n" +
            "  \"tariffAmount\": 0,\n" +
            "  \"idAdjustment\": \"string\",\n" +
            "  \"payerAnswer\": \"string\",\n" +
            "  \"transactionCode\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
            "  \"transactionDate\": \"2022-04-28T17:19:18.570Z\",\n" +
            "  \"pixType\": \"WITHDRAWAL\",\n" +
            "  \"purchaseAmount\": 0,\n" +
            "  \"changeAmount\": 0,\n" +
            "  \"agentWithdrawalIspb\": \"string\",\n" +
            "  \"agentMode\": \"AGPSS\"\n" +
            "}"

    private val changeQrCodeResponse = "{\n" +
            "  \"idAccountType\": \"string\",\n" +
            "  \"idAccount\": \"string\",\n" +
            "  \"transactionType\": \"string\",\n" +
            "  \"idEndToEnd\": \"string\",\n" +
            "  \"transferType\": 3,\n" +
            "  \"transactionStatus\": \"string\",\n" +
            "  \"debitParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"errorType\": \"string\",\n" +
            "  \"errorCode\": \"string\",\n" +
            "  \"errorMessage\": \"string\",\n" +
            "  \"amount\": 0,\n" +
            "  \"creditParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"finalAmount\": 0,\n" +
            "  \"tariffAmount\": 0,\n" +
            "  \"idAdjustment\": \"string\",\n" +
            "  \"payerAnswer\": \"string\",\n" +
            "  \"transactionCode\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
            "  \"transactionDate\": \"2022-04-28T17:19:18.570Z\",\n" +
            "  \"pixType\": \"CHANGE\",\n" +
            "  \"purchaseAmount\": 0,\n" +
            "  \"changeAmount\": 0,\n" +
            "  \"agentWithdrawalIspb\": \"string\",\n" +
            "  \"agentMode\": \"AGPSS\"\n" +
            "}"

    private val transferQrCodeResponse = "{\n" +
            "  \"idAccountType\": \"string\",\n" +
            "  \"idAccount\": \"string\",\n" +
            "  \"transactionType\": \"string\",\n" +
            "  \"idEndToEnd\": \"string\",\n" +
            "  \"transferType\": 2,\n" +
            "  \"transactionStatus\": \"string\",\n" +
            "  \"debitParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"errorType\": \"string\",\n" +
            "  \"errorCode\": \"string\",\n" +
            "  \"errorMessage\": \"string\",\n" +
            "  \"amount\": 0,\n" +
            "  \"creditParty\": {\n" +
            "    \"ispb\": \"string\",\n" +
            "    \"bankName\": \"string\",\n" +
            "    \"name\": \"string\",\n" +
            "    \"bankAccountType\": \"string\",\n" +
            "    \"nationalRegistration\": \"string\",\n" +
            "    \"bankAccountNumber\": \"string\",\n" +
            "    \"bankBranchNumber\": \"string\",\n" +
            "    \"key\": \"string\"\n" +
            "  },\n" +
            "  \"finalAmount\": 0,\n" +
            "  \"tariffAmount\": 0,\n" +
            "  \"idAdjustment\": \"string\",\n" +
            "  \"payerAnswer\": \"string\",\n" +
            "  \"transactionCode\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\n" +
            "  \"transactionDate\": \"2022-04-28T17:19:18.570Z\",\n" +
            "  \"pixType\": \"TRANSFER\",\n" +
            "  \"purchaseAmount\": 0,\n" +
            "  \"changeAmount\": 0,\n" +
            "  \"agentWithdrawalIspb\": \"string\",\n" +
            "  \"agentMode\": \"AGPSS\"\n" +
            "}"

    @Mock
    lateinit var view: PixTransferReceiptContract.View

    @Mock
    lateinit var repository: PixTransferRepositoryContract

    private lateinit var presenter: PixTransferReceiptPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = spy(
            PixTransferReceiptPresenter(
                view,
                repository,
                uiScheduler, ioScheduler
            )
        )
    }

    @Test
    fun `when calling onValidateDetails and the detailsTransfer parameter is null, it must call onGetDetails and when it returns successfully, it'll go to showPaymentFlow`() {
        val captorIsShowLoading = argumentCaptor<Boolean>()
        val captor = argumentCaptor<TransferDetailsResponse>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()

        val response = Gson().fromJson(changeQrCodeResponse, TransferDetailsResponse::class.java)

        val successBalance = Observable.just(response)
        doReturn(successBalance).whenever(repository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onValidateDetails(CODE, END_ID, null)

        verify(view).showLoading()
        verify(presenter).onGetDetails(
            codeCaptor.capture(),
            endIdCaptor.capture(),
            captorIsShowLoading.capture()
        )
        verify(view).hideLoading()
        verify(presenter).showPaymentFlow(captor.capture())
        verify(view, never()).showError(any())

        assertEquals(END_ID, endIdCaptor.firstValue)
        assertEquals(CODE, codeCaptor.firstValue)
        assertEquals(false, captorIsShowLoading.firstValue)
        assertTrue(captor.allValues.contains(response))
    }

    @Test
    fun `when calling onValidateDetails and transferType is KEY it shows onShowCommonTransfer`() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val keyResponseMock = Gson().fromJson(keyTransferResponse, TransferDetailsResponse::class.java)

        presenter.onValidateDetails(CODE, END_ID, keyResponseMock)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowCommonTransfer(captor.capture())
        verify(view, never()).onShowWithdrawReceipt(any())
        verify(view, never()).onShowChangeReceipt(any())
        verify(view, never()).onShowQrCodePaymentReceipt(any())
        verify(view, never()).showError(any())
        verify(presenter, never()).onGetDetails(any(), any(), any())

        assertTrue(captor.allValues.contains(keyResponseMock))
    }

    @Test
    fun `when calling onValidateDetails and transferType is MANUAL it shows onShowManualTransferReceipt `() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val manualResponse = Gson().fromJson(manualResponse, TransferDetailsResponse::class.java)

        presenter.onValidateDetails(CODE, END_ID, manualResponse)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowManualTransferReceipt(captor.capture())
        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowWithdrawReceipt(any())
        verify(view, never()).onShowChangeReceipt(any())
        verify(view, never()).onShowQrCodePaymentReceipt(any())
        verify(view, never()).showError(any())
        verify(presenter, never()).onGetDetails(any(), any(), any())

        assertTrue(captor.allValues.contains(manualResponse))
    }

    @Test
    fun `when calling onValidateDetails and transferType is STATIC QR CODE  and pixType is WITHDRAWAL it shows onShowWithdrawReceipt `() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val withdrawQrCodeResponseMock =
            Gson().fromJson(withdrawQrCodeResponse, TransferDetailsResponse::class.java)

        presenter.onValidateDetails(CODE, END_ID, withdrawQrCodeResponseMock)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowWithdrawReceipt(captor.capture())
        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowChangeReceipt(any())
        verify(view, never()).onShowQrCodePaymentReceipt(any())
        verify(view, never()).onShowManualTransferReceipt(any())
        verify(view, never()).showError(any())
        verify(presenter, never()).onGetDetails(any(), any(), any())

        assertTrue(captor.allValues.contains(withdrawQrCodeResponseMock))
    }

    @Test
    fun `when calling onValidateDetails and transferType is DYNAMIC QR CODE  and pixType is CHANGE it shows onShowChangeReceipt `() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val changeQrCodeResponseMock =
            Gson().fromJson(changeQrCodeResponse, TransferDetailsResponse::class.java)

        presenter.onValidateDetails(CODE, END_ID, changeQrCodeResponseMock)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowChangeReceipt(captor.capture())
        verify(view, never()).onShowWithdrawReceipt(any())
        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowQrCodePaymentReceipt(any())
        verify(view, never()).onShowManualTransferReceipt(any())
        verify(view, never()).showError(any())
        verify(presenter, never()).onGetDetails(any(), any(), any())

        assertTrue(captor.allValues.contains(changeQrCodeResponseMock))
    }

    @Test
    fun `when calling onValidateDetails and transferType is STATIC QR CODE  and pixType is TRANSFER it shows onShowQrCodePaymentReceipt `() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val transferQrCodeResponseMock = Gson().fromJson(transferQrCodeResponse, TransferDetailsResponse::class.java)

        presenter.onValidateDetails(CODE, END_ID, transferQrCodeResponseMock)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowQrCodePaymentReceipt(captor.capture())
        verify(view, never()).onShowChangeReceipt(any())
        verify(view, never()).onShowWithdrawReceipt(any())
        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowManualTransferReceipt(any())
        verify(view, never()).showError(any())
        verify(presenter, never()).onGetDetails(any(), any(), any())

        assertTrue(captor.allValues.contains(transferQrCodeResponseMock))
    }

    @Test
    fun `when calling onValidateDetails and the detailsTransfer parameter is null, it should call onGetDetails and when it returns error , it shows showError`() {
        val captorIsShowLoading = argumentCaptor<Boolean>()
        val captor = argumentCaptor<ErrorMessage>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()

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
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onValidateDetails(CODE, END_ID, null)

        verify(view).showLoading()
        verify(presenter).onGetDetails(codeCaptor.capture(), endIdCaptor.capture(), captorIsShowLoading.capture())
        verify(view).hideLoading()
        verify(view).showError(captor.capture())

        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowQrCodePaymentReceipt(any())
        verify(view, never()).onShowChangeReceipt(any())
        verify(view, never()).onShowWithdrawReceipt(any())
        verify(view, never()).onShowCommonTransfer(any())
        verify(view, never()).onShowManualTransferReceipt(any())

        assertEquals(END_ID, endIdCaptor.firstValue)
        assertEquals(CODE, codeCaptor.firstValue)
        assertEquals(false, captorIsShowLoading.firstValue)
        assertEquals(500, captor.firstValue.httpStatus)
    }
}