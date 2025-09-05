package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class AccessToken(
        @SerializedName("expiresIn")
        val expiresIn: Int,
        @SerializedName("token")
        val token: String
)