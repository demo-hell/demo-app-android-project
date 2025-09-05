package br.com.mobicare.cielo.arv.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvScheduledAnticipation(
    val token: String? = null,
    val rateSchedules: List<RateSchedules?>? = null,
    val domicile: List<ArvBank?>? = null

): Parcelable

@Keep
@Parcelize
data class RateSchedules(
    val name: String? = null,
    val schedule: Boolean? = null,
    val rate: Double? = null,
    val cnpjRoot: Boolean? = null,
    val cnpjBranch: Boolean? = null
): Parcelable