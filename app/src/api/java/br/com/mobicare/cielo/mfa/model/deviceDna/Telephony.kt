package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Telephony(
        @SerializedName("carrierIsoCountryCode")
        val carrierIsoCountryCode: String,
        @SerializedName("carrierMobileCountryCode")
        val carrierMobileCountryCode: Any,
        @SerializedName("carrierMobileNetworkCode")
        val carrierMobileNetworkCode: String,
        @SerializedName("carrierName")
        val carrierName: String,
        @SerializedName("cellIpAddress")
        val cellIpAddress: Any,
        @SerializedName("imeiNumber")
        val imeiNumber: Any,
        @SerializedName("isRoamingNetwork")
        val isRoamingNetwork: Boolean,
        @SerializedName("networkType")
        val networkType: String,
        @SerializedName("phoneType")
        val phoneType: String,
        @SerializedName("simIsoCountryCode")
        val simIsoCountryCode: String,
        @SerializedName("simOperatorName")
        val simOperatorName: String,
        @SerializedName("simSerialNumber")
        val simSerialNumber: Any,
        @SerializedName("subscriberId")
        val subscriberId: Any
)