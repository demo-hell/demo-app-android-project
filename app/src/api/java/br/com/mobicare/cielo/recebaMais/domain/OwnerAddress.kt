package br.com.mobicare.cielo.recebaMais.domain

data class OwnerAddress(
    val addressTypes: List<Any>,
    val city: String,
    val id: String,
    val neighborhood: String,
    val state: String,
    val streetAddress: String,
    val streetAddress2: String,
    val types: List<Any>,
    val zipCode: String,
    val number: String
)