package br.com.mobicare.cielo.machine.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrdersAvailabilityResponse(
        val availabilityList: List<Availability>
) : Parcelable

@Parcelize
data class Availability(
        val code: String,
        val initialWeekDay: String,
        val finalWeekDay: String,
        val initialHour: Int,
        val finalHour: Int
) : Parcelable
