package br.com.mobicare.cielo.main.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MenuTarget(
    @SerializedName("external")
    val external: Boolean = false,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("mail")
    val mail: String? = null,
    @SerializedName("url")
    val url: String? = null
) : Parcelable