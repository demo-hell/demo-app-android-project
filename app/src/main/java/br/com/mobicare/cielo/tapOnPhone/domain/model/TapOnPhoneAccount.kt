package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TapOnPhoneAccount (
    val bankNumber: String,
    val bankName: String,
    val imgSource: String,
    val agency: String,
    val account: String,
    val accountDigit: String
) : Parcelable