package br.com.mobicare.cielo.interactbannersoffers.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class HiringOffers(
    val id: Int? = null,
    val name: String? = null,
    val priority: Int? = null,
    val treatmentCode: String? = null,
    val customerId: Long? = null,
    val hiringUrl: String? = null,
    val internal: Boolean? = null,
    val serialNumber: String? = null,
    val steps: List<String>? = null,
    val factors: List<Factor>? = null,
    val debitVisaFee: Double? = null,
    val creditVisaFee: Double? = null,
    val fewInstallmentsVisaFee: Double? = null,
    val installmentsVisaFee: Double? = null,
    val debitMasterFee: Double? = null,
    val creditMasterFee: Double? = null,
    val fewInstallmentsMasterFee: Double? = null,
    val installmentsMasterFee: Double? = null,
    val creditDinersFee: Double? = null,
    val fewInstallmentsDinersFee: Double? = null,
    val installmentsDinersFee: Double? = null,
    val debitEloFee: Double? = null,
    val creditEloFee: Double? = null,
    val fewInstallmentsEloFee: Double? = null,
    val installmentsEloFee: Double? = null,
    val creditHiperFee: Double? = null,
    val fewInstallmentsHiperFee: Double? = null,
    val installmentsHiperFee: Double? = null,
    val creditAmexFee: Double? = null,
    val fewInstallmentsAmexFee: Double? = null,
    val installmentsAmexFee: Double? = null,
    val creditFactorGetFastMensal: Double? = null,
    val installmentFactorGetFastMensal: Double? = null,
    val equipmentQuantity: Int? = null,
    val rentValue: Double? = null,
    val rentValueUnreached: Double? = null,
    val billingGoal: Double? = null,
    val defaultRentValue: Double? = null,
    val hasChangeGetFast: Boolean? = null,
    val hasChangeMDR: Boolean? = null,
    val initialDate: String? = null,
    val descriptionId: Int? = null,
    val pixRate: Double? = null,
    val surplusTarget: Double? = null,
    val optinStatement: Boolean? = null,
    val creditRateBefore: Double? = null,
    val creditRateAfter: Double? = null,
    val rateInstallmentsBefore: Double? = null,
    val rateInstallmentsAfter: Double? = null,
) : Parcelable

@Parcelize
data class Factor(
    val rate: Int? = null,
    val type: String? = null,
) : Parcelable
