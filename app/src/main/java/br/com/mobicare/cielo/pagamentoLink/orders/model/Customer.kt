package br.com.mobicare.cielo.pagamentoLink.orders.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Customer (

	val name : String? = null,
	val document : Document? = null,
	val email : String? = null,
	val phone : String? = null
) : Parcelable