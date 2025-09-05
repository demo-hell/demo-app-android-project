package br.com.mobicare.cielo.pix.domain

data class QRCodeChargeRequest(
    val city: String? = null,
    val expirationDate: String?,
    val txId: String?,
    val key: String?,
    val message: String?,
    val originalAmount: Double?
)