package br.com.mobicare.cielo.chargeback.data.model.response

import androidx.annotation.Keep

@Keep
data class ChargebackDocumentSenderResponse(
    val dateInclusion: String,
    val nameFile: String,
    val code: Int,
    val message: String,
    val fileBase64: String
)