package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class RevokeClaimsResponse(
    val cancellationReason: String?,
    val claimId: String?,
    val claimStatus: String?,
    val lastModificationDate: String?
) : Parcelable