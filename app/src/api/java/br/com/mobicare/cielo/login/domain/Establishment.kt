package br.com.mobicare.cielo.login.domain

import com.google.gson.annotations.SerializedName

data class Establishment(
        @SerializedName("ec") val ec: String,
        @SerializedName("matrix") val matrix: String,
        @SerializedName("cnpj") val cnpj: String,
        @SerializedName("tradeName") val tradeName: String,
        @SerializedName("status") val status: String,
        @SerializedName("antecipationCategory") val antecipationCategory: String
)