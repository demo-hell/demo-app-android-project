package br.com.mobicare.cielo.meuCadastroNovo.domain


import com.google.gson.annotations.SerializedName

data class CepAddressResponse(
    @SerializedName("userAddresses")
    val addresses: List<CepAdress>
)