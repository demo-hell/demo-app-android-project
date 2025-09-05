package br.com.mobicare.cielo.balcaoRecebiveisExtrato.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NegotiationsBanks(
    val items: List<BankItem>? = null
): Parcelable

@Parcelize
data class BankItem(
    val code: String? = null,
    val name: String? = null,
    val agency: String? = null,
    val agencyDigit: String? = null,
    val account: String? = null,
    val accountDigit: String? = null,
    val quantity: Int? = 0,
    val netAmount: Double? = 0.0,
    val depositStatusHistory: List<BankStatus>? = null,
    val lastDepositStatus: List<BankStatus>? = null
): Parcelable

@Parcelize
data class BankStatus(
    val paymentStatusDate: String? = null,
    val paymentStatusCode: String? = null,
    val paymentStatus: String? = null
): Parcelable
