package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixMyLimitsResponse(
    val merchantNumber: String?,
    val limits: List<Limits>?
) : Parcelable

@Keep
@Parcelize
data class Limits(
    val type: String?,
    val servicesGroup: String?,
    val defaultLimit: Double?,
    val accountLimit: Double?,
    val requestLimit: Double?,
    val requestDate: String?,
    val beneficiaryType: String?,
    val transactionLimit: Double?
) : Parcelable