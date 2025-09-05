package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixExtractResponse(
    var items: MutableList<PixExtractItem>? = null,
    var totalItemsPage: Int? = null
) : Parcelable

@Keep
@Parcelize
data class PixExtractItem(
    var receipts: MutableList<PixExtractReceipt>? = null,
    val title: String? = null,
    val yearMonth: String? = null
) : Parcelable

@Keep
@Parcelize
data class PixExtractReceipt(
    val title: String? = null,
    val amount: Double? = null,
    val changeAmount: Double? = null,
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
    val transactionDate: String? = null,
    val transactionStatus: String? = null,
    val transactionType: String? = null,
    val transferType: String? = null,
    val schedulingDate: String? = null,
    val schedulingCode: String? = null,
    val period: String? = null
) : Parcelable