package br.com.mobicare.cielo.orders.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderReplacementResponse(val id: String? = null, val hours: Int? = null) : Parcelable