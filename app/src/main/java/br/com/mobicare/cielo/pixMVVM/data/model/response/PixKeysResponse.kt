package br.com.mobicare.cielo.pixMVVM.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.domain.PixClaimDetail
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixKeysResponse(
    val keys: KeyList?,
    val claims: KeyList?
) : Parcelable {

    @Keep
    @Parcelize
    data class KeyList(
        val date: String?,
        val count: Int?,
        val keys: List<KeyItem>?
    ) : Parcelable

    @Keep
    @Parcelize
    data class KeyItem(
        val key: String?,
        val keyType: String?,
        val claimType: String?,
        val main: Boolean?,
        val claimDetail: PixClaimDetail?
    ) : Parcelable

    @Keep
    @Parcelize
    data class ClaimDetail(
        val canceledBy: String?,
        val cancellationReason: String?,
        val claimId: String?,
        val claimStatus: String?,
        val claimType: String?,
        val completionLimitDate: String?,
        val confirmationReason: String?,
        val key: String?,
        val keyOwningRevalidationRequired: Boolean?,
        val keyType: String?,
        val lastModifiedDate: String?,
        val participationType: String?,
        val resolutionLimitDate: String?,
        val claimantIspbName: String?,
        val donorIspbName: String?
    ) : Parcelable

}