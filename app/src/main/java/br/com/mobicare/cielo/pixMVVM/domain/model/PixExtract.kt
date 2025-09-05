package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import kotlinx.android.parcel.Parcelize
import java.time.ZonedDateTime

@Keep
@Parcelize
data class PixExtract(
    var items: List<PixExtractItem>,
    var totalItemsPage: Int? = null,
) : Parcelable {
    @Keep
    @Parcelize
    data class PixExtractItem(
        var receipts: List<PixExtractReceipt>,
        val title: String? = null,
        val yearMonth: String? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class PixExtractReceipt(
        val title: String? = null,
        val amount: Double? = null,
        val changeAmount: Double? = null,
        val date: ZonedDateTime? = null,
        val finalAmount: Double? = null,
        val idAccount: Int? = null,
        val idAdjustment: Int? = null,
        val idCorrelation: String? = null,
        val idEndToEnd: String? = null,
        val idEndToEndOriginal: String? = null,
        val payeeName: String? = null,
        val payerAnswer: String? = null,
        val payerName: String? = null,
        val pixType: String? = null,
        val purchaseAmount: Double? = null,
        val reversalCode: Int? = null,
        val reversalCodeDescription: String? = null,
        val tariffAmount: Double? = null,
        val transactionCode: String? = null,
        val transactionDate: ZonedDateTime? = null,
        val transactionStatus: String? = null,
        val transactionType: String? = null,
        val transferType: String? = null,
        val schedulingDate: ZonedDateTime? = null,
        val schedulingCode: String? = null,
        val period: String? = null,
        val type: PixExtractReceiptType? = null,
    ) : Parcelable
}
