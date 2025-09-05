package br.com.mobicare.cielo.meusCartoes.domains.entities

import com.google.gson.annotations.SerializedName

data class Statement(
        @SerializedName("transactionTime") val dateHourTransaction: String,
        @SerializedName("merchant") val establishment: String,
        @SerializedName("transactionType") val operationType: String,
        @SerializedName("amount") val amount: Double
)