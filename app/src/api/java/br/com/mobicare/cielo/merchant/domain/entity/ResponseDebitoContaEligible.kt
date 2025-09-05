package br.com.mobicare.cielo.merchant.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ResponseDebitoContaEligible(
    val status: String?,
    val document: String?,
    val login: String?,
    val merchant:String?
): Parcelable

data class Bank(
    val account: String,
    val accountDigit: String,
    val accountType: Int,
    val agency: String,
    val agencyDigit: String,
    val code: String
)