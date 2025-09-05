package br.com.mobicare.cielo.solesp.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SolespRequest(
    val initialDate: String? = null,
    val finalDate: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val customerRequest: String? = null,
    val reason: String? = null
) : Parcelable