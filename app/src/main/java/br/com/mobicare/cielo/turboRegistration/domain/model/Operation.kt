package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Operation(
    val label: String? = null,
    val value: String? = null,
    val isLegalEntity: Boolean? = null,
    val isSavingsAccount: Boolean? = null
) : Parcelable
