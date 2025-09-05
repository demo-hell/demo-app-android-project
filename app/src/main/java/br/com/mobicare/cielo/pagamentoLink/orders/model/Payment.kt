package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payment(

        val price: Double? = null,
        val installments: Int? = null,
        val date: String? = null,
        val status: String? = null,
        val statusDescription: String? = null,
        val type: String? = null,
        val typeDescription: String? = null,
        val antifraud: AntiFraud? = null
) : Parcelable