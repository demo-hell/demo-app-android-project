package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvSingleNegotiationParams(
    val negotiationType: String? = null,
    val initialDate: String? = null,
    val finalDate: String? = null
) : Parcelable

