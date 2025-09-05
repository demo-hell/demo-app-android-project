package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class DeviceDna(
        @SerializedName("deviceSignature")
        val deviceSignature: DeviceSignature,
        @SerializedName("ipAddress")
        val ipAddress: String
)