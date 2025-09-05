package br.com.mobicare.cielo.meusCartoes.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CardSituation(
    @SerializedName("type") val type: String? = null,
    @SerializedName("description") val situation: String? = null
) : Parcelable
