package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import br.com.mobicare.cielo.pix.enums.PixRevokeClaimsEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConfirmClaimsRequest(
    val claimId: String?,
    val reason: String = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
    val verificationCode: String? = null
) : Parcelable
