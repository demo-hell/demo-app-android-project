package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransferResponse(
        @SerializedName("fee")
        val fee: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("id")
        val transferId: String
) : Parcelable