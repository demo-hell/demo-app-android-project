package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Owner(@SerializedName("phones") val phones: List<Phone>) : Parcelable