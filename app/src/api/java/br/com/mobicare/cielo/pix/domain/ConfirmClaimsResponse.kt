package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
class ConfirmClaimsResponse(
        val claimId: String?,
        val confirmationReason: String?,
        val claimStatus: String?,
        val lastModificationDate: String?
) : Parcelable