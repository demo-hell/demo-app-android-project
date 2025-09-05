package br.com.mobicare.cielo.turboRegistration.data.model.response

data class AddressResponse(
    val city: String? = null,
    val country: String? = null,
    val description: String? = null,
    val neighborhood: String? = null,
    val number: String? = null,
    val purposeAddress: List<PurposeAddress>? = null,
    val state: String? = null,
    val streetAddress: String? = null,
    val streetAddress2: String? = null,
    val types: List<String>? = null,
    val zipCode: String? = null
)