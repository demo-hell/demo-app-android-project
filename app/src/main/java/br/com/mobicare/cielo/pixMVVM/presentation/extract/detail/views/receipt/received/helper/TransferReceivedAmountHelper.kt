package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.received.helper

import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class TransferReceivedAmountHelper(private val data: PixTransferDetail) {

    val hasFee get() = data.fee != null

    val isFeePendingOrProcessing get() = data.fee?.feeTransactionStatus in listOf<PixTransactionStatus?>(
        PixTransactionStatus.PENDING,
        PixTransactionStatus.PROCESSING
    )

    val isSettlementProcessing get() =
        data.settlement?.settlementTransactionStatus in listOf<PixTransactionStatus?>(
            PixTransactionStatus.PENDING,
            PixTransactionStatus.PROCESSING,
            PixTransactionStatus.NOT_EXECUTED,
            PixTransactionStatus.SENT_WITH_ERROR,
        )

    private val isFeeExecuted get() =
        data.fee?.feeTransactionStatus == PixTransactionStatus.EXECUTED

    val isSettlementExecuted get() =
        data.settlement?.settlementTransactionStatus == PixTransactionStatus.EXECUTED

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

    val formattedTariffAmount get() =
        data.tariffAmount?.let { (-it).toPtBrWithNegativeRealString() }

    val formattedSettlementFinalAmount get() =
        data.settlement?.settlementFinalAmount?.toPtBrRealString()

}