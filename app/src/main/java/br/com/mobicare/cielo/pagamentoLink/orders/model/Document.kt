package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Document (

	val type : String? = null,
	val id : String? = null
) : Parcelable