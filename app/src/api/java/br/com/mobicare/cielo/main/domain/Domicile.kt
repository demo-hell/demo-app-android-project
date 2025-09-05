package br.com.mobicare.cielo.main.domain


import com.google.gson.annotations.SerializedName

data class Domicile(
    @SerializedName("account")
    val account: String,
    @SerializedName("agency")
    val agency: String,
    @SerializedName("cardAssociations")
    val cardAssociations: List<String>,
    @SerializedName("code")
    val code: String,
    @SerializedName("merchantBankCode")
    val merchantBankCode: String
)