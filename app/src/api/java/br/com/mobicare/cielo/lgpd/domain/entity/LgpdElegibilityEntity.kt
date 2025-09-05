package br.com.mobicare.cielo.lgpd.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LgpdElegibilityEntity(
    @SerializedName("eligible")
    val eligible: Boolean?,
    @SerializedName("digitalAccount")
    val digitalAccount: Boolean?,
    @SerializedName("merchantOwner")
    val owner: Boolean?
) : Parcelable