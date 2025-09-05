package br.com.mobicare.cielo.pix.domain

data class QRCodeDecodeRequest(
    val qrCode: String,
    val paymentDateIntended: String? = null
)