package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class DeviceSignature(
        @SerializedName("bluetooth")
        val bluetooth: Bluetooth,
        @SerializedName("browser")
        val browser: Browser,
        @SerializedName("camera")
        val camera: Camera,
        @SerializedName("collector")
        val collector: String,
        @SerializedName("collectorVersion")
        val collectorVersion: String,
        @SerializedName("extra")
        val extra: Extra,
        @SerializedName("location")
        val location: Location,
        @SerializedName("screen")
        val screen: Screen,
        @SerializedName("system")
        val deviceSystem: DeviceSystem,
        @SerializedName("telephony")
        val telephony: Telephony,
        @SerializedName("wifi")
        val wifi: Wifi
)