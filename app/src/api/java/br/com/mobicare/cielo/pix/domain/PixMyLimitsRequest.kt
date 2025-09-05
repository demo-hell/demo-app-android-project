package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixMyLimitsRequest (
        val serviceGroup: String?,
        val limits: MutableList<LimitsRequest>?,
        val fingerprint: String? = null,
        val beneficiaryType: String? = null
) : Parcelable

@Keep
@Parcelize
data class LimitsRequest(
        val type: String?,
        var value: Double?
) : Parcelable
