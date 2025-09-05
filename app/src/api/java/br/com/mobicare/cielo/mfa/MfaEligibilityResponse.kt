package br.com.mobicare.cielo.mfa

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MfaEligibilityResponse(
    @SerializedName("status") val status: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("typeCode") val typeCode: Int?,
    @SerializedName("statusCode") val statusCode: String?,
    @SerializedName("statusTrace") val statusTrace: String?
) : Parcelable