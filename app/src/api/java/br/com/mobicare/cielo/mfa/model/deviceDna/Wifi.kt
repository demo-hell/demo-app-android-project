package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Wifi(
        @SerializedName("broadcastAddress")
        val broadcastAddress: String,
        @SerializedName("connected")
        val connected: Boolean,
        @SerializedName("gatewayAddress")
        val gatewayAddress: String,
        @SerializedName("ipAddress")
        val ipAddress: String,
        @SerializedName("macAddress")
        val macAddress: String,
        @SerializedName("netmaskAddress")
        val netmaskAddress: String,
        @SerializedName("userEnabled")
        val userEnabled: Boolean
)