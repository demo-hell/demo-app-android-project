package br.com.mobicare.cielo.openFinance.data.model.request

data class GivenUpShareRequest(
    val shareId: String,
    val errorDescription: String,
    val requestId: String
)
