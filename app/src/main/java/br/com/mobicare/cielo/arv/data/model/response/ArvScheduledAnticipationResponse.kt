package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize


@Keep
@Parcelize
data class ArvScheduledAnticipationResponse(
    val token: String? = null,
    val rateSchedules: List<RateSchedulesResponse?>? = null,
    val domicile: List<ArvBankResponse?>? = null
): Parcelable

@Keep
@Parcelize
data class RateSchedulesResponse(
    val name: String? = null,
    val schedule: Boolean? = null,
    val rate: Double? = null,
    val cnpjRoot: Boolean? = null,
    val cnpjBranch: Boolean? = null
): Parcelable