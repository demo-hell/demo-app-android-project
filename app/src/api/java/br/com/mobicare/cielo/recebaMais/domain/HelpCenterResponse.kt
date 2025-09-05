package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HelpCenterResponse(@SerializedName("helpCenter") val helpCenter : List<HelpCenter>) : Parcelable