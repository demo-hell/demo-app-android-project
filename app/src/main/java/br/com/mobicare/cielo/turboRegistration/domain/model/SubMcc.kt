package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubMcc(
    val code: Int? = null,
    val description: String? = null
) : Parcelable
