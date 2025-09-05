package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ClaimsRequest(
    val claimType: String?,
    val key: String?,
    val keyType: String?,
    val verificationCode: String? = null
) : Parcelable