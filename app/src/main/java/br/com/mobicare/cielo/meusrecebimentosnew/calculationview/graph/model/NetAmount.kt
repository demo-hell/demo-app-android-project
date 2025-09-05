package br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NetAmount(val date: String, val netAmount: Double): Parcelable