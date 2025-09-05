package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixRefundReceiptsResponse(
    val currentPage: Int?,
    val last: Boolean?,
    val totalPages: Int?,
    val totalAmountPossibleReversal: Double?,
    val totalItemsPage: Int?,
    val items: List<ReceiptYearMonth>
) {

    data class ReceiptYearMonth(
        val receipts: List<ReceiptItem>
    )

    data class ReceiptItem(
        val idAccount: Int?,
        val idEndToEnd: String?,
        val idEndToEndOriginal: String?,
        val transactionDate: String?,
        val transactionType: String?,
        val transactionStatus: String?,
        val reversalCode: Int?,
        val reversalReason: String?,
        val tariffAmount: Double?,
        val amount: Double?,
        val finalAmount: Double?,
        val idAdjustment: Int?,
        val transactionCode: String?,
    )

}