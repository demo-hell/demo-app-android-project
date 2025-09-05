package br.com.mobicare.cielo.newRecebaRapido.domain.model

data class OfferSummary(
    val brandCode: Int,
    val brandName: String,
    val cashFee: Double?,
    val installments: List<InstallmentSummary>?
)

data class InstallmentSummary(
    val number: Int,
    val fee: Double?
)