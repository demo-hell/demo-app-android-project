package br.com.mobicare.cielo.merchant.domain.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantPermissionsEligible(
    @SerializedName("eligible")
    val eligible: Boolean?
) : Parcelable