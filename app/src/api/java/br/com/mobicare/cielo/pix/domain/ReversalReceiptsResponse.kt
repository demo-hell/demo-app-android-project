package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ReversalReceiptsResponse(
    val currentPage: Int?,
    val last: Boolean?,
    val totalPages: Int?,
    val totalAmountPossibleReversal: Double?,
    val totalItemsPage: Int?,
    val items: List<Receipt>
) : Parcelable

@Keep
@Parcelize
data class Receipt(
    val title: String?,
    val yearMonth: String?,
    val receipts: List<ReceiptItem>
) : Parcelable

@Keep
@Parcelize
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
) : Parcelable
