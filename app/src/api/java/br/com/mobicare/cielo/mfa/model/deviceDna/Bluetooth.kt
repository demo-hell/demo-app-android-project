package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Bluetooth(
        @SerializedName("macAddress")
        val macAddress: Any,
        @SerializedName("supported")
        val supported: Any
)