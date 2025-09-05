package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class AntiFraud(val status: String? = null, val description: String? = null) : Parcelable