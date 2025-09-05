package br.com.mobicare.cielo.solesp.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SolespResponse(
    val id: String? = null,
    val reason: String? = null,
    val isNew: Boolean? = null,
    val dataSolicitacao: String? = null
) : Parcelable