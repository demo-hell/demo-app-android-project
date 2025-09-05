package br.com.mobicare.cielo.pix.ui.extract.detail.views.helpers

import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum

class CreditAmountDataHelper(private val data: TransferDetailsResponse) {

    val hasFee get() = data.fee != null
    private val hasSettlement get() = data.settlement != null
    val hasFeeOrSettlement get() = hasFee || hasSettlement

    val isFeePendingOrProcessing get() = data.fee?.feeTransactionStatus in listOf<String?>(
        PixTransactionStatusEnum.PENDING.name,
        PixTransactionStatusEnum.PROCESSING.name
    )

    val isSettlementProcessing get() =
        data.settlement?.settlementTransactionStatus in listOf<String?>(
            PixTransactionStatusEnum.PENDING.name,
            PixTransactionStatusEnum.PROCESSING.name,
            PixTransactionStatusEnum.NOT_EXECUTED.name,
            PixTransactionStatusEnum.SENT_WITH_ERROR.name,
        )

    private val isFeeExecuted get() =
        data.fee?.feeTransactionStatus == PixTransactionStatusEnum.EXECUTED.name

    val isSettlementExecuted get() =
        data.settlement?.settlementTransactionStatus == PixTransactionStatusEnum.EXECUTED.name

    val isSettlementCompletelyExecuted get() = data.settlement?.run {
        isSettlementExecuted && settlementFinalAmount != null && settlementIdEndToEnd.isNullOrBlank().not()
    } ?: false

    val isSettlementPartiallyExecuted get() = data.settlement?.run {
        isSettlementExecuted && settlementFinalAmount != null && settlementIdEndToEnd.isNullOrBlank()
    } ?: false

    val isFeeClickable get() = data.fee?.run {
        feeIdEndToEnd != null && feeTransactionCode != null && isFeeExecuted
    } ?: false

    val isNetAmountClickable get() = data.settlement?.run {
        isSettlementCompletelyExecuted && settlementTransactionCode != null
    } ?: false

    val isTransferOperationType get() =
        data.pixType == PixQRCodeOperationTypeEnum.TRANSFER.name && data.transferType in listOf<Int?>(
            PixTransferTypeEnum.MANUAL.code,
            PixTransferTypeEnum.CHAVE.code,
            PixTransferTypeEnum.QR_CODE_ESTATICO.code,
            PixTransferTypeEnum.QR_CODE_DINAMICO.code
        )

    val isChangeOperationType get() =
        data.pixType == PixQRCodeOperationTypeEnum.CHANGE.name && data.transferType in listOf<Int?>(
            PixTransferTypeEnum.QR_CODE_ESTATICO.code,
            PixTransferTypeEnum.QR_CODE_DINAMICO.code
        )

    val formattedTariffAmount get() =
        data.tariffAmount?.let { (-it).toPtBrWithNegativeRealString() }

    val formattedSettlementFinalAmount get() =
        data.settlement?.settlementFinalAmount?.toPtBrRealString()

}