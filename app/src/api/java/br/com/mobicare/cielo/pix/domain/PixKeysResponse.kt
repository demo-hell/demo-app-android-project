package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixKeysResponse(
        @SerializedName("keys")
        val key: Key?,
        val claims: Key?
) : Parcelable

@Keep
@Parcelize
data class Key(
        val count: Int?,
        val date: String?,
        val keys: List<MyKey>?
) : Parcelable

@Keep
@Parcelize
data class PixClaimDetail(
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