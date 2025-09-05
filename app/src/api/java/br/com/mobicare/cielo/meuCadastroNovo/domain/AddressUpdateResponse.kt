package br.com.mobicare.cielo.meuCadastroNovo.domain


import com.google.gson.annotations.SerializedName

data class AddressUpdateResponse(
    @SerializedName("message")
    val message: String
)