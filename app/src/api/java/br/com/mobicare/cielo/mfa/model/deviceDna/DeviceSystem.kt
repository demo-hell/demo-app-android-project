package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class DeviceSystem(
        @SerializedName("androidId")
        val androidId: String,
        @SerializedName("board")
        val board: String,
        @SerializedName("brand")
        val brand: String,
        @SerializedName("cpuAbi")
        val cpuAbi: String,
        @SerializedName("cpuAbi2")
        val cpuAbi2: Any,
        @SerializedName("debuggerAttached")
        val debuggerAttached: Boolean,
        @SerializedName("deviceName")
        val deviceName: String,
        @SerializedName("hardware")
        val hardware: String,
        @SerializedName("host")
        val host: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("jailBroken")
        val jailBroken: Boolean,
        @SerializedName("locale")
        val locale: String,
        @SerializedName("manufacturer")
        val manufacturer: String,
        @SerializedName("model")
        val model: String,
        @SerializedName("numberOfProcessors")
        val numberOfProcessors: Int,
        @SerializedName("osVersion")
        val osVersion: String,
        @SerializedName("platform")
        val platform: String,
        @SerializedName("processName")
        val processName: String,
        @SerializedName("radio")
        val radio: String,
        @SerializedName("serial")
        val serial: String,
        @SerializedName("systemName")
        val systemName: String,
        @SerializedName("tags")
        val tags: String,
        @SerializedName("timeZone")
        val timeZone: String,
        @SerializedName("totalDiskSpace")
        val totalDiskSpace: String,
        @SerializedName("totalMemory")
        val totalMemory: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("user")
        val user: String
)