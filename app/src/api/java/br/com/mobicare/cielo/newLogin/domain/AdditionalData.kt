package br.com.mobicare.cielo.newLogin.domain

import com.google.gson.annotations.SerializedName

data class AdditionalData(
    @SerializedName("newDeviceDetected")
    val newDeviceDetected: Boolean = false,
    @SerializedName("foreign")
    val foreign: Boolean = false
)