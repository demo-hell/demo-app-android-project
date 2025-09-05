package br.com.mobicare.cielo.arv.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ArvBranchesContracts(
    val total: Int? = null,
    val schedules: List<Schedules>? = null
) : Parcelable


@Parcelize
data class Schedules(
    val cnpj: String? = null,
    val name: String? = null,
    val nominalRateCielo: Double? = null,
    val contractDateCielo: String? = null,
    val nominalRateMarket: Double? = null,
    val contractDateMarket: String? = null
) : Parcelable
