package br.com.mobicare.cielo.selfRegistration.domains


import com.google.gson.annotations.SerializedName

data class SelfRegistrationResponse(
    @SerializedName("email")
    val email: String,
    @SerializedName("tokenExpirationInMinutes")
    val tokenExpirationInMinutes: Int
)