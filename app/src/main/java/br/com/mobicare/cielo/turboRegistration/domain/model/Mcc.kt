package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Mcc(
    val idAddress: String? = null,
    val code: Int? = null,
    val description: String? = null,
    val subMcc: List<SubMcc>? = null
) : Parcelable
