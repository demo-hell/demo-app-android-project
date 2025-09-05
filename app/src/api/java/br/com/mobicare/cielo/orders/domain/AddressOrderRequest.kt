package br.com.mobicare.cielo.orders.domain

data class AddressOrderRequest(
    val streetAddress: String? = null,
    val streetAddress2: String? = null,
    val neighborhood: String? = null,
    val number: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zipCode: String? = null,
    val storefront: String? = null,
    val landmark: String? = null
)