package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Bank(
    val code: String? = null,
    val name: String? = null,
) : Parcelable
