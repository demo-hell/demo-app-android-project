package br.com.mobicare.cielo.chargeback.domain.model

import androidx.annotation.Keep

@Keep
data class ChargebackDocumentSender(
    val dateInclusion: String,
    val nameFile: String,
    val code: Int,
    val message: String,
    val fileBase64: String
)
