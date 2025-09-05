package br.com.mobicare.cielo.merchant.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantChallengerActivateRequest(
    @SerializedName("activationCode")
    val activateCode: String? = null,
    val fingerprint: String? = null
) : Parcelable

@Parcelize
data class CpfUserAuthorization(
    val cpf: String
) : Parcelable