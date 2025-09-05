package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transfer(
        @SerializedName("destination") var destination: Destination,
        @SerializedName("amount") var amount: Double,
        @SerializedName("description") var description: String
) : Parcelable