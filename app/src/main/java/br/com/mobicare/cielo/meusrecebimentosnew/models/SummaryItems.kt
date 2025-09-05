package br.com.mobicare.cielo.meusrecebimentosnew.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SummaryItems (
        val code : Int,
        val type : String,
        val netAmount : Double,
        val links : List<Link>
) : Parcelable