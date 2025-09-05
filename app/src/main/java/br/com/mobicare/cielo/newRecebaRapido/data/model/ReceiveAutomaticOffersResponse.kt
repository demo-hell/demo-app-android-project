package br.com.mobicare.cielo.newRecebaRapido.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OfferResponse (
    val id: String? = null,
    val periodicity: String? = null,
    val validityPeriodDate: String? = null,
    val validityPeriodMonths: String? = null,
    val items: List<OfferItemResponse>?,
    val factors: List<FactorResponse>?,
    val summary: List<SummaryResponse>?
) : Parcelable

@Keep
@Parcelize
data class FactorResponse (
    val rate: Double?,
    val type: String?,
    val number: Long? = null
) : Parcelable

@Keep
@Parcelize
data class OfferItemResponse (
    val brandCode: Long?,
    val brandDescription: String?,
    val imgCardBrand: String?,
    val items: List<CreditOfferItemResponse>? = null,
) : Parcelable

@Keep
@Parcelize
data class CreditOfferItemResponse (
    val recebaRapidoMdr: Double? = null,
    val mdr: Double? = null,
    val summarizedMdr: Double? = null,
    val type: String? =  null,
    val productCode: Int? = null,
    val description: String? = null,
    val installments: List<InstallmentOfferItemResponse>? = null
) : Parcelable

@Keep
@Parcelize
data class InstallmentOfferItemResponse (
    val recebaRapidoMdr: Double?,
    val mdr: Double?,
    val summarizedMdr: Double?,
    val number: Long?
) : Parcelable

@Keep
@Parcelize
data class SummaryResponse (
    val mdr: Double?,
    val settlementDay: Long?,
    val type: String?,
    val typeDescription: String?,
    val settlementDayDescription: String?
) : Parcelable
