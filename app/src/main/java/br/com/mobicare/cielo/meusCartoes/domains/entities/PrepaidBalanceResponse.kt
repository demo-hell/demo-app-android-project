package br.com.mobicare.cielo.meusCartoes.domains.entities

import com.google.gson.annotations.SerializedName

data class PrepaidBalanceResponse (
    @SerializedName("currency") val currency: String,
    @SerializedName("amount") val amount: Double
)