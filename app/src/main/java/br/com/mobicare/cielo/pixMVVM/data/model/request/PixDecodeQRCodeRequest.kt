package br.com.mobicare.cielo.pixMVVM.data.model.request

data class PixDecodeQRCodeRequest(
    val qrCode: String? = null,
    val paymentDateIntended: String? = null,
)
