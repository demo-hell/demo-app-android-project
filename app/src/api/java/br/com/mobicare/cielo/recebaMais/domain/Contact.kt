package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(@SerializedName("email") val emails: List<String>) : Parcelable