package br.com.mobicare.cielo.newRecebaRapido.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Offer (
    val id: String?,
    val periodicity: String?,
    val validityPeriodDate: String? = null,
    val validityPeriodMonths: String? = null,
    val items: List<OfferItem>?,
    val factors: List<Factor>?,
    val summary: List<Summary>?
) : Parcelable

@Keep
@Parcelize
data class Factor (
    val rate: Double?,
    val type: String?,
    val number: Long? = null
): Parcelable

@Keep
@Parcelize
data class OfferItem (
    val brandCode: Long?,
    val brandDescription: String?,
    val imgCardBrand: String?,
    val items: List<CreditOfferItem>? = null,
): Parcelable

@Keep
@Parcelize
data class CreditOfferItem (
    val recebaRapidoMdr: Double? = null,
    val mdr: Double? = null,
    val summarizedMdr: Double? = null,
    val type: String? = null,
    val productCode: Int? = null,
    val description: String? = null,
    val installments: List<InstallmentOfferItem>? = null
): Parcelable

@Keep
@Parcelize
data class InstallmentsOfferItems(
    val type: String?,
    val installments: List<InstallmentOfferItem>?
): Parcelable

@Keep
@Parcelize
data class InstallmentOfferItem (
    val recebaRapidoMdr: Double?,
    val mdr: Double?,
    val summarizedMdr: Double?,
    val number: Long?
): Parcelable

@Keep
@Parcelize
data class Summary (
    val mdr: Double?,
    val settlementDay: Long?,
    val type: String?,
    val typeDescription: String?,
    val settlementDayDescription: String?
): Parcelable

@Keep
@Parcelize
data class TextCalendar(
    val name: String? = null,
) : Parcelable

@Keep
@Parcelize
data class CardBrandDTO(
    val name: String? = null,
) : Parcelable

@Keep
@Parcelize
data class GeneralOfferSummary(
    val referenceBrand: String? = null,
    val validityPeriodType: String? = null,
    val validityPeriod: String?
) : Parcelable

@Keep
@Parcelize
data class SelectedPlanSummary(
    val monthDaySelected: Int?,
    val weekDaySelected: String?,
    val periodicitySelected: String,
    val typeTransactionSelected: String
) : Parcelable