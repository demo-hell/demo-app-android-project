package br.com.mobicare.cielo.main.domain


import com.google.gson.annotations.SerializedName

data class Pricing(
    @SerializedName("maxRate")
    val maxRate: Double?,
    @SerializedName("rate")
    val rate: Double?,
    @SerializedName("type")
    val type: String
)