package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvHistoricResponse(
    val items: List<Item?>?,
    val pagination: Pagination?
): Parcelable

@Keep
@Parcelize
data class Item(
    val discountAmount: Double? = null,
    val grossAmount: Double? = null,
    val modality: String? = null,
    val negotiationDate: String? = null,
    val negotiationFee: Double? = null,
    val negotiationType: String? = null,
    val netAmount: Double? = null,
    val operationNumber: String? = null,
    val status: String? = null
): Parcelable

@Keep
@Parcelize
data class Pagination(
    val firstPage: Boolean? = null,
    val lastPage: Boolean? = null,
    val numPages: Int? = null,
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val totalElements: Int? = null
): Parcelable