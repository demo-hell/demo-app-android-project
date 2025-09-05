package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvBranchContractResponse(
    val total: Int? = null,
    val schedules: List<SchedulesResponse>? = null
) : Parcelable

@Keep
@Parcelize
data class SchedulesResponse(
    val cnpj: String? = null,
    val name: String? = null,
    val nominalRateCielo: Double? = null,
    val contractDateCielo: String? = null,
    val nominalRateMarket: Double? = null,
    val contractDateMarket: String? = null
) : Parcelable
