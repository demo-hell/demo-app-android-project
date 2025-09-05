package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class OwnerContact(
        @SerializedName("id")
    val id: Int,
        @SerializedName("name")
    val name: String,
        @SerializedName("phones")
    val ownerPhones: List<OwnerPhone>,
        @SerializedName("types")
    val types: List<String>
)