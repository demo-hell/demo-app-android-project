package br.com.mobicare.cielo.taxaPlanos.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TaxaPlanosDetailsResponse (
    val merchant: String,
    val totalRevenue: Double,
    val maximumAllowedRevenue: Double,
    val exceedingRevenue: Double,
    val exceedingRevenueFee: Double,
    val minimumRequiredRevenue: Double,
    val remainingRevenueToExemption: Double,
    val exempted: Boolean
) : Parcelable