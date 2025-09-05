package br.com.mobicare.cielo.p2m.domain.model

data class Taxes(
    val id: Int,
    val period: String,
    val type: String,
    val values: List<Value>
)