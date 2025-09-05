package br.com.mobicare.cielo.pagamentoLink.domain


import com.google.gson.annotations.SerializedName

data class LinkPaymentEligibilityResponse(
    @SerializedName("contracted")
    val contracted: Boolean,
    @SerializedName("eligible")
    val eligible: Boolean,
    @SerializedName("name")
    val name: String
)