package br.com.mobicare.cielo.mfa.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MfaEnrollmentRequest(
    @SerializedName("activationCode")
    val activationCode: String?,
    val fingerprint: String? = null
) : Parcelable