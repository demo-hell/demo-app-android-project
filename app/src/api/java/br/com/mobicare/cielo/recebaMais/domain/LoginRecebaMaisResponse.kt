package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class LoginRecebaMaisResponse(
        @SerializedName("accessToken")
        val accessToken: AccessToken,
        @SerializedName("refreshToken")
        val refreshToken: RefreshToken
)