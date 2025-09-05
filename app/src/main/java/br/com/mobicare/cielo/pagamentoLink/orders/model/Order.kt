package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Order(

        val id: String? = null,
        val code: String? = null,
        val merchantOrderCode: String? = null,
        val date: String? = null,
        val shipping: Shipping? = null,
        val customer: Customer? = null,
        val payment: Payment? = null
) : Parcelable