package br.com.mobicare.cielo.pixMVVM.domain.model

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import java.time.LocalDate
import java.util.Calendar

data class PixReceiptsScheduled(
    val last: Boolean? = null,
    val totalItemsPage: Int? = null,
    val lastSchedulingIdentifierCode: String = EMPTY,
    val lastNextDateTimeScheduled: Calendar? = null,
    val items: List<Item>? = null,
) {
    data class Item(
        val title: String = EMPTY,
        val yearMonth: String = EMPTY,
        val receipts: List<Receipt>? = null,
    ) {
        data class Receipt(
            val transactionStatus: String = EMPTY,
            val finalAmount: Double? = null,
            val payeeName: String = EMPTY,
            val payeeDocumentNumber: String = EMPTY,
            val payeeBankName: String = EMPTY,
            val schedulingCode: String = EMPTY,
            val schedulingDate: LocalDate? = null,
            val type: PixExtractReceiptType? = null,
        )
    }
}
