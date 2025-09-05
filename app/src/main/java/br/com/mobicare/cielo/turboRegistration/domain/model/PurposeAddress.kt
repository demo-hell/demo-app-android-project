package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Purpose(
    val contact: Int? = null,
    val name: String? = null,
    val type: Int? = null
): Parcelable