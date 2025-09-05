package br.com.mobicare.cielo.newLogin.domain

import br.com.mobicare.cielo.me.MeResponse
import com.google.gson.annotations.SerializedName

data class LoginResponse(
        @SerializedName("access_token")
        val accessToken: String,
        @SerializedName( "expires_in")
        val expiresIn: Int,
        @SerializedName("refresh_token")
        val refreshToken: String,
        @SerializedName("token_type")
        val tokenType: String,
        @SerializedName("isConvivenciaUser")
        val isConvivenciaUser: Boolean,
        @SerializedName("me")
        val me: MeResponse?,
        @SerializedName("additionalData")
        val additionalData: AdditionalData?
)