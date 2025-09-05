package br.com.mobicare.cielo.mfa.model.deviceDna


import com.google.gson.annotations.SerializedName

data class Camera(
        @SerializedName("autoFocus")
        val autoFocus: Any,
        @SerializedName("flash")
        val flash: Any,
        @SerializedName("frontCamera")
        val frontCamera: Any,
        @SerializedName("frontCameraSupportedFormats")
        val frontCameraSupportedFormats: Any,
        @SerializedName("frontCameraSupportedSizes")
        val frontCameraSupportedSizes: Any,
        @SerializedName("numberOfCameras")
        val numberOfCameras: Any,
        @SerializedName("rearCamera")
        val rearCamera: Any,
        @SerializedName("rearCameraSupportedFormats")
        val rearCameraSupportedFormats: Any,
        @SerializedName("rearCameraSupportedSizes")
        val rearCameraSupportedSizes: Any
)