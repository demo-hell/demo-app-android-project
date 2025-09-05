package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixQrCodeOperationType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferOrigin
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransferType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.result.PixTransferUiResult

class PixTransferResultHandler {

    private lateinit var data: PixTransferDetail

    operator fun invoke(data: PixTransferDetail): PixTransferUiResult {
        this.data = data

        return if (isFeeTransaction)
            getFeeTransferResult()
        else if (isAutomaticTransferTransaction)
            getAutomaticTransferResult()
        else
            getTransferResult()
    }

    private val isFeeTransaction get() = data.run {
        transactionType == PixTransactionType.TRANSFER_DEBIT
                && pixType == PixQrCodeOperationType.FEE
    }

    private val isAutomaticTransferTransaction get() = data.run {
        transactionType == PixTransactionType.TRANSFER_DEBIT
                && pixType == PixQrCodeOperationType.TRANSFER
                && transferOrigin == PixTransferOrigin.SETTLEMENT_V2
    }

    private fun getFeeTransferResult() = when (data.transactionStatus) {
        PixTransactionStatus.PENDING -> PixTransferUiResult.FeeTransferInProcess
        PixTransactionStatus.EXECUTED -> PixTransferUiResult.FeeTransferSent
        else -> PixTransferUiResult.FeeTransferCanceled
    }

    private fun getAutomaticTransferResult() = when (data.transactionStatus) {
        PixTransactionStatus.PENDING -> PixTransferUiResult.AutomaticTransferInProcess
        PixTransactionStatus.EXECUTED -> PixTransferUiResult.AutomaticTransferSent
        else -> PixTransferUiResult.AutomaticTransferCanceled
    }

    private fun getTransferResult() = when (data.transactionStatus) {
        PixTransactionStatus.PENDING -> PixTransferUiResult.TransferInProcess()
        PixTransactionStatus.EXECUTED -> getTransferExecutedResult()
        else -> PixTransferUiResult.TransferCanceled()
    }

    private fun getTransferExecutedResult() = when (data.transferType) {
        PixTransferType.QR_CODE_ESTATICO,
        PixTransferType.QR_CODE_DINAMICO -> getQrCodeTransferExecutedResult()
        else -> getTransferSentOrReceivedResult(
            debit = { PixTransferUiResult.TransferSent() },
            credit = { PixTransferUiResult.TransferReceived() }
        )
    }

    private fun getQrCodeTransferExecutedResult() = when (data.pixType) {
        PixQrCodeOperationType.WITHDRAWAL -> getTransferSentOrReceivedResult(
            debit = { PixTransferUiResult.QrCodeWithdrawalTransferSent },
            credit = { PixTransferUiResult.QrCodeWithdrawalTransferReceived }
        )
        PixQrCodeOperationType.CHANGE -> getTransferSentOrReceivedResult(
            debit = { PixTransferUiResult.QrCodeChangeTransferSent },
            credit = { PixTransferUiResult.QrCodeChangeTransferReceived }
        )
        else -> getTransferSentOrReceivedResult(
            debit = { PixTransferUiResult.QrCodeTransferSent() },
            credit = { PixTransferUiResult.QrCodeTransferReceived() }
        )
    }

    private fun getTransferSentOrReceivedResult(
        debit: () -> PixTransferUiResult,
        credit: () -> PixTransferUiResult
    ) = if (data.transactionType == PixTransactionType.TRANSFER_CREDIT) {
        credit.invoke()
    } else {
        debit.invoke()
    }

}