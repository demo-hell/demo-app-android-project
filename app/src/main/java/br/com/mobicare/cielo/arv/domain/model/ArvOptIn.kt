package br.com.mobicare.cielo.arv.domain.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArvOptIn(
    val eligible: Boolean? = null
) : Parcelable