package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class UserOwner(
    @SerializedName("birthDate")
    val birthDate: String,
    @SerializedName("cpf")
    val cpf: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phones")
    val phones: List<OwnerPhone>
)