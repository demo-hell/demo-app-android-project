package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Browser(
        @SerializedName("userAgent")
        val userAgent: String
)