package br.com.mobicare.cielo.pagamentoLink.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreateLinkBodyResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String
) : Parcelable