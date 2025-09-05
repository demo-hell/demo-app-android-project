package br.com.mobicare.cielo.meuCadastroNovo.domain


import com.google.gson.annotations.SerializedName

data class CepAdress(
    @SerializedName("address")
    val address: String,
    @SerializedName("addressCode")
    val addressCode: String,
    @SerializedName("addressType")
    val addressType: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("neighborhood")
    val neighborhood: String,
    @SerializedName("state")
    val state: String
)