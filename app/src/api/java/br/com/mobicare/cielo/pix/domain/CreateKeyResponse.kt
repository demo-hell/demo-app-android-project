package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CreateKeyResponse(
    val claimOpeningDate: String,
    val key: String,
    val ownershipDate: String,
    val creationDate: String
) : Parcelable