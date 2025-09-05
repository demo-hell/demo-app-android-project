package br.com.mobicare.cielo.pixMVVM.data.model.response

import androidx.annotation.Keep

@Keep
data class PixValidateKeyResponse(
    val accountNumber: String? = null,
    val accountType: String? = null,
    val branch: String? = null,
    val claimType: String? = null,
    val creationDate: String? = null,
    val endToEndId: String? = null,
    val key: String? = null,
    val keyType: String? = null,
    val ownerDocument: String? = null,
    val ownerName: String? = null,
    val ownerTradeName: String? = null,
    val ownerType: String? = null,
    val ownershipDate: String? = null,
    val participant: String? = null,
    val participantName: String? = null,
)