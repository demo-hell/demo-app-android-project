package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneOrderRequest (
    val offerId: String,
    val sessionId: String,
    val payoutData: PayoutData,
    val agreements: List<Agreement>,
    val itemsConfigurations: List<String>
): Parcelable

@Keep
@Parcelize
data class Agreement (
    val code: String,
    val value: String
): Parcelable

@Keep
@Parcelize
data class PayoutData (
    val payoutMethod: String,
    val targetBankAccount: TargetBankAccount
): Parcelable

@Keep
@Parcelize
data class TargetBankAccount (
    val bankNumber: String,
    val agency: String,
    val accountNumber: String,
    val accountType: String
): Parcelable