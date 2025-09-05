package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(

    val streetAddress: String? = null,
    val neighborhood: String? = null,
    val city: String? = null,
    val number: String? = null,
    val state: String? = null,
    val zipCode: Int? = null
) : Parcelable