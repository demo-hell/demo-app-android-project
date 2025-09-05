package br.com.mobicare.cielo.mfa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankEnrollmentResponse(
    val status: String
) : Parcelable