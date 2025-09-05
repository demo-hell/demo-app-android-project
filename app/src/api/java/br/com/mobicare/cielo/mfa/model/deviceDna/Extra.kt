package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Extra(
        @SerializedName("account")
        val account: Any,
        @SerializedName("securityPolicy")
        val securityPolicy: Boolean
)