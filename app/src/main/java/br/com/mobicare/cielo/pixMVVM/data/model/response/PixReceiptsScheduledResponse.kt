package br.com.mobicare.cielo.pixMVVM.data.model.response

data class PixReceiptsScheduledResponse(
    val last: Boolean? = null,
    val totalItemsPage: Int? = null,
    val lastSchedulingIdentifierCode: String? = null,
    val lastNextDateTimeScheduled: String? = null,
    val items: List<Item>? = null,
) {
    data class Item(
        val title: String? = null,
        val yearMonth: String? = null,
        val receipts: List<Receipt>? = null,
    ) {
        data class Receipt(
            val transactionStatus: String? = null,
            val finalAmount: Double? = null,
            val payeeName: String? = null,
            val payeeDocumentNumber: String? = null,
            val payeeBankName: String? = null,
            val schedulingCode: String? = null,
            val scheduledDate: String? = null,
            val type: String? = null,
        )
    }
}
