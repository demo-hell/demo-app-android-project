package br.com.mobicare.cielo.pixMVVM.domain.model

import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionType
import java.time.ZonedDateTime

data class PixRefundReceipts(
    val totalAmountPossibleReversal: Double? = null,
    val totalItemsPage: Int? = null,
    val receipts: List<ReceiptItem>? = null
) {

    data class ReceiptItem(
        val idAccount: Int? = null,
        val idEndToEnd: String? = null,
        val idEndToEndOriginal: String? = null,
        val transactionDate: ZonedDateTime? = null,
        val transactionType: PixTransactionType? = null,
        val transactionStatus: PixTransactionStatus? = null,
        val reversalCode: Int? = null,
        val reversalReason: String? = null,
        val tariffAmount: Double? = null,
        val amount: Double? = null,
        val finalAmount: Double? = null,
        val idAdjustment: Int? = null,
        val transactionCode: String? = null,
    )

}