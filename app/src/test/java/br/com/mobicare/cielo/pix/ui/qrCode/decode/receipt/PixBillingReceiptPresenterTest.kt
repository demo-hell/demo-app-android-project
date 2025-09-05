package br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt

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

class PixBillingReceiptPresenterTest {

    @Mock
    lateinit var view: PixBillingReceiptContract.View

    @Mock
    lateinit var repository: PixTransferRepositoryContract

    private lateinit var presenter: PixBillingReceiptPresenter

    private val uiScheduler = Schedulers.trampoline()
    private val ioScheduler = Schedulers.trampoline()

    private val transferDetaisResponse = "{\n" +
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

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixBillingReceiptPresenter(
            view,
            repository,
            uiScheduler, ioScheduler
        )
    }

    @Test
    fun `when calling onGetReceipt and it returns successfully, show onShowReceipt`() {
        val captor = argumentCaptor<TransferDetailsResponse>()
        val endIdCaptor = argumentCaptor<String>()
        val codeCaptor = argumentCaptor<String>()

        val response = Gson().fromJson(transferDetaisResponse, TransferDetailsResponse::class.java)

        val successBalance = Observable.just(response)
        doReturn(successBalance).whenever(repository)
            .getTransferDetails(endIdCaptor.capture(), codeCaptor.capture())

        presenter.onGetReceipt(CODE, END_ID)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).onShowReceipt(captor.capture())
        verify(view, never()).showError(any())

        assertEquals(
            END_ID,
            endIdCaptor.firstValue
        )
        assertEquals(
            CODE,
            codeCaptor.firstValue
        )
        assertTrue(captor.allValues.contains(response))
    }

    @Test
    fun `when calling onGetReceipt and it returns with error, show showError`() {
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

        presenter.onGetReceipt(CODE, END_ID)

        verify(view).showLoading()
        verify(view).hideLoading()
        verify(view).showError(captor.capture())
        verify(view, never()).onShowReceipt(any())

        assertEquals(
            END_ID,
            endIdCaptor.firstValue
        )
        assertEquals(
            CODE,
            codeCaptor.firstValue
        )
        assertEquals(
            500,
            captor.firstValue.httpStatus
        )
    }
}