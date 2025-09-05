package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ValidateKeyResponse(
    val accountNumber: String?,
    val accountType: String?,
    val branch: String?,
    val claimType: String?,
    val creationDate: String?,
    val endToEndId: String,
    val key: String,
    val keyType: String,
    val ownerDocument: String?,
    val ownerName: String?,
    val ownerTradeName: String?,
    val ownerType: String?,
    val ownershipDate: String?,
    val participant: String?,
    val participantName: String?
) : Parcelable