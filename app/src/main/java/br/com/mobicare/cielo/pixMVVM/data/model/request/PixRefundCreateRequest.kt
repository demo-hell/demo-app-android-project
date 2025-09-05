package br.com.mobicare.cielo.pixMVVM.data.model.request

data class PixRefundCreateRequest(
    val idEndToEndOriginal: String? = null,
    val amount: Double? = null,
    val reversalReason: String? = null,
    val idTx: String? = null,
    val payerAnswer: String? = null,
    val fingerprint: String? = null
)
