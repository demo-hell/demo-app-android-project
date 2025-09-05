package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Bank(val name: String?,
                val code: String,
                val agency: String,
                val agencyDigit: String,
                val accountNumber: String,
                val accountDigit: String,
                val imageURL: String,
                val isPrepaid: Boolean) : Parcelable