package br.com.mobicare.cielo.meuCadastroDomicilio.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountTransferRequest(
    val destination: Destination,
    val origins: List<Origin>
) : Parcelable

@Parcelize
data class BankAccount(
        val account: String,
        val accountDigit: String?,
        val agency: String,
        val agencyDigit: String?,
        val code: String,
        val savingsAccount: Boolean
) : Parcelable


@Parcelize
data class CardBrand(
        val code: Int
) : Parcelable

@Parcelize
data class Destination(
        val account: String,
        val accountDigit: String,
        val agency: String,
        val agencyDigit: String?,
        val code: String,
        val savingsAccount: Boolean
) : Parcelable

@Parcelize
data class Origin(
        val bankAccount: BankAccount,
        val cardBrands: List<CardBrand>?
) : Parcelable