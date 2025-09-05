package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentType(val value: String, val name: String) : Parcelable