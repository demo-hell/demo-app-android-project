package br.com.mobicare.cielo.mfa

import com.google.gson.annotations.SerializedName

data class EnrollmentResponse(
        @SerializedName("status") val status: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("typeCode") val typeCode: Int?,
        @SerializedName("statusCode") val statusCode: String?,
        @SerializedName("statusTrace") val statusTrace: String?
)