package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixTimeManagementResponse(
    val merchantNumber: String? = null,
    val nighttimeStart: String? = null,
    val actualDayTimeDescription: String? = null,
    val actualNightTimeDescription: String? = null,
    val lastRequest: LastRequest? = null
) : Parcelable

@Keep
@Parcelize
data class LastRequest(
    val nighttimeStart: String? = null,
    val status: String? = null,
    val requestDate: String? = null
) : Parcelable