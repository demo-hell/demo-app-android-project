package br.com.mobicare.cielo.main.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TargetX(
    @SerializedName("external")
    val haveMoreItems: Boolean
) : Parcelable