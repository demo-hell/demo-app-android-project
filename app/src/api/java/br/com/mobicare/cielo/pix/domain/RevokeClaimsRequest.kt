package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import br.com.mobicare.cielo.pix.enums.PixRevokeClaimsEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RevokeClaimsRequest(
    val claimId: String?,
    val isClaimer: Boolean = true,
    val reason: String = PixRevokeClaimsEnum.CLIENT_SOLICITATION.name,
    val verificationCode: String? = null
): Parcelable