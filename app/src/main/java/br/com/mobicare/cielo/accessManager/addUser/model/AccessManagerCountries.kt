package br.com.mobicare.cielo.accessManager.addUser.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class AccessManagerCountries(
    val countries: List<Country>
) : Parcelable

@Parcelize
@Keep
data class Country(
    val code: String? = null,
    val name: String? = null,
    val ddi: String? = null
) : Parcelable
