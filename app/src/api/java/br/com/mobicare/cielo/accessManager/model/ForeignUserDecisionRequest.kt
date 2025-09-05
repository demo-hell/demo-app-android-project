package br.com.mobicare.cielo.accessManager.model

data class ForeignUserDecisionRequest(
    val userId: String,
    val decision: String
)