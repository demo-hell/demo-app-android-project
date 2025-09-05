package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Shipping(

        val type: String? = null,
        val typeDescription: String? = null,
        val status: String? = null,
        val statusDescription: String? = null,
        val address: Address? = null,
        val allowDeliverer: Boolean = false,
        val allowTracking: Boolean = false
) : Parcelable