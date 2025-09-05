package br.com.mobicare.cielo.openFinance.domain.model

data class InfoDetailsShare(
    val function: String,
    val consentId: String,
    val shareId: String,
    val flow: String,
    val deadLine: DeadLine?
)
