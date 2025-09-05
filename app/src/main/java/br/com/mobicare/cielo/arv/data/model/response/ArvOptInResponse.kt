package br.com.mobicare.cielo.arv.data.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ArvOptInResponse(
    val eligible: Boolean? = null
) : Parcelable