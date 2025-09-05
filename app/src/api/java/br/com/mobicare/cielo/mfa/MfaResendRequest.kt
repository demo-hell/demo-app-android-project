package br.com.mobicare.cielo.mfa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MfaResendRequest(
    val account: String? = null,
    val accountAvailable: Boolean? = null,
    val accountDigit: String? = null,
    val accountMessage: String? = null,
    val accountType: String? = null,
    val agency: String? = null,
    val agencyDigit: String? = null,
    val bankCode: String? = null,
    val bankName: String? = null,
    val identificationNumber: String? = null,
    val imgSource: String? = null,
    val legalEntity: String? = null,
    val fingerprint: String? = null
) : Parcelable