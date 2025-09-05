package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ClaimsResponse(
    val claimId: String,
    val claimStatus: String,
    val lastModifiedDate: String,
    val resolutionLimitDate: String?
) : Parcelable