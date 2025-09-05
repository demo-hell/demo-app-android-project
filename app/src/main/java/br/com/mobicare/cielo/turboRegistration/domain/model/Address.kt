package br.com.mobicare.cielo.turboRegistration.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Address(
    val city: String? = null,
    val country: String? = null,
    val description: String? = null,
    val neighborhood: String? = null,
    val number: String? = null,
    val purposeAddress: List<Purpose>? = null,
    val state: String? = null,
    val streetAddress: String? = null,
    val streetAddress2: String? = null,
    val types: List<String>? = null,
    val zipCode: String? = null
) : Parcelable
