package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferOrigin
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult
import br.com.mobicare.cielo.pixMVVM.utils.PixTransactionsFactory
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PixTransferResultHandlerTest {

    private val entity = PixTransactionsFactory.TransferDetail.entity

    private val handler = PixTransferResultHandler()

    @Test
    fun `it should return PixTransferUiResult_FeeTransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.FEE,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.FeeTransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_FeeTransferInProcess result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.FEE,
                transactionStatus = PixTransactionStatus.PENDING
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.FeeTransferInProcess::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_FeeTransferCanceled result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.FEE,
                transactionStatus = PixTransactionStatus.FAILED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.FeeTransferCanceled::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_AutomaticTransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.TRANSFER,
                transferOrigin = PixTransferOrigin.SETTLEMENT_V2,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.AutomaticTransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_AutomaticTransferInProcess result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.TRANSFER,
                transferOrigin = PixTransferOrigin.SETTLEMENT_V2,
                transactionStatus = PixTransactionStatus.PENDING
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.AutomaticTransferInProcess::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_AutomaticTransferCanceled result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.TRANSFER,
                transferOrigin = PixTransferOrigin.SETTLEMENT_V2,
                transactionStatus = PixTransactionStatus.FAILED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.AutomaticTransferCanceled::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_TransferInProcess result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionStatus = PixTransactionStatus.PENDING
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.TransferInProcess::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_TransferCanceled result`() {
        val result = handler.invoke(
            entity.copy(
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionStatus = PixTransactionStatus.FAILED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.TransferCanceled::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_TransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                transferType = PixTransferType.MANUAL,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.TransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_TransferReceived result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionType = PixTransactionType.TRANSFER_CREDIT,
                transferType = PixTransferType.MANUAL,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.TransferReceived::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeTransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeTransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeTransferReceived result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.TRANSFER,
                transactionType = PixTransactionType.TRANSFER_CREDIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeTransferReceived::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeChangeTransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.CHANGE,
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeChangeTransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeChangeTransferReceived result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.CHANGE,
                transactionType = PixTransactionType.TRANSFER_CREDIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeChangeTransferReceived::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeWithdrawalTransferSent result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                transactionType = PixTransactionType.TRANSFER_DEBIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeWithdrawalTransferSent::class.java)
    }

    @Test
    fun `it should return PixTransferUiResult_QrCodeWithdrawalTransferReceived result`() {
        val result = handler.invoke(
            entity.copy(
                pixType = PixQrCodeOperationType.WITHDRAWAL,
                transactionType = PixTransactionType.TRANSFER_CREDIT,
                transferType = PixTransferType.QR_CODE_DINAMICO,
                transactionStatus = PixTransactionStatus.EXECUTED
            )
        )

        assertThat(result).isInstanceOf(PixTransferUiResult.QrCodeWithdrawalTransferReceived::class.java)
    }

}