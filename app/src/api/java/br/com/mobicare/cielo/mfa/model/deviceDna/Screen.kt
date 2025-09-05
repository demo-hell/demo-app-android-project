package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Screen(
        @SerializedName("displayId")
        val displayId: String,
        @SerializedName("height")
        val height: String,
        @SerializedName("orientation")
        val orientation: String,
        @SerializedName("width")
        val width: String
)