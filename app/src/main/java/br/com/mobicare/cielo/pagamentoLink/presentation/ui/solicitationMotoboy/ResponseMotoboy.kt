package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy

data class ResponseMotoboy(
    val orderId: String,
    val status: String,
    val statusDescription: String,
    val deliveryStatus: String,
    val trackingUrl: String
)