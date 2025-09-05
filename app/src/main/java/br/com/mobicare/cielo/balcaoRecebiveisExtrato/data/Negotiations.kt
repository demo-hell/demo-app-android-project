package br.com.mobicare.cielo.balcaoRecebiveisExtrato.data

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Negotiations(
    val items: List<Item>? = null,
    val pagination: Pagination? = null,
    val summary: Summary? = null
) : Parcelable

@Keep
@Parcelize
data class Item(
    val averageTermRunningDays: Int? = 0,
    val averageTermWorkingDays: Int? = 0,
    val date: String = "",
    val discountAmount: Double? = 0.0,
    val grossAmount: Double? = 0.0,
    val identificationNumber: String? = "",
    val merchantId: String? = "",
    val negotiationFee: Double? = 0.0,
    val netAmount: Double? = 0.0,
    val operationCipNumber: String? = "",
    val operationNumber: String? = "",
    val operationSourceCode: Int? = 0,
    val paymentNode: Int? = 0,
    val paymentScheduleDate: String? = ""
) : Parcelable

@Keep
@Parcelize
data class Pagination(
    val firstPage: Boolean? = false,
    val lastPage: Boolean? = false,
    val numPages: Int? = 0,
    val pageNumber: Int? = 0,
    val pageSize: Int? = 0,
    val totalElements: Int? = 0
) : Parcelable

@Keep
@Parcelize
data class Summary(
    val totalAmount: Double,
    val totalDiscountAmount: Double,
    val totalNetAmount: Double,
    val totalQuantity: Int
) : Parcelable