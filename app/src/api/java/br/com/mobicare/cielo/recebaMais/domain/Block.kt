package br.com.mobicare.cielo.recebaMais.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Block(
        @SerializedName("codeType")
        val codeType: Int? = null,
        @SerializedName("nameType")
        val nameType: String? = null,
        @SerializedName("codeReason")
        val codeReason: Int? = null,
        @SerializedName("descriptionReason")
        val descriptionReason: String? = null,
        @SerializedName("dateBeginBlocked")
        val dateBeginBlocked: String? = null,
        @SerializedName("nameRequestorBlocked")
        val nameRequestorBlocked: String? = null

): Parcelable