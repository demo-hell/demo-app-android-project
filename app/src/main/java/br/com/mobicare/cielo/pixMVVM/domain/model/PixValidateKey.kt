package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixValidateKey(
    val accountNumber: String,
    val accountType: String,
    val branch: String,
    val claimType: String,
    val creationDate: String,
    val endToEndId: String,
    val key: String,
    val keyType: String,
    val ownerDocument: String,
    val ownerName: String,
    val ownerTradeName: String,
    val ownerType: String,
    val ownershipDate: String,
    val participant: String,
    val participantName: String,
) : Parcelable