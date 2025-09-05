package br.com.mobicare.cielo.mfa

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankAccount(
    val bankName: String,
    val bankCode: String,
    val agency: String,
    val account: String,
    val accountDigit: String,
    val accountType: String,
    val imgSource: String
) : Parcelable