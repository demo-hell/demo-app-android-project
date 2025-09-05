package br.com.mobicare.cielo.commons.data.domain


import com.google.gson.annotations.SerializedName

data class MultichannelUserTokenResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("tokenExpirationInMinutes")
    val tokenExpirationInMinutes: Int
)