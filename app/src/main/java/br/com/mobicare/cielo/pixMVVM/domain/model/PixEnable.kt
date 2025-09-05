package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixEnable(
    val refund: Boolean? = null,
    val cancelSchedule: Boolean? = null,
    val requestAnalysis: Boolean? = null,
) : Parcelable
