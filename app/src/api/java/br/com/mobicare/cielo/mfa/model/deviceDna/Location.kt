package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Location(
        @SerializedName("latitude")
        val latitude: Any,
        @SerializedName("longitude")
        val longitude: Any
)