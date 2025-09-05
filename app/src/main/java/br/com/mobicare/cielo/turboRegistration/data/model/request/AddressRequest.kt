package br.com.mobicare.cielo.turboRegistration.data.model.request

import br.com.mobicare.cielo.commons.constants.BR


data class AddressRequest(
    val streetAddress: String? = null,
    val streetAddress2: String? = null,
    val neighborhood: String? = null,
    val state: String? = null,
    val city: String? = null,
    val zipCode: String? = null,
    val number: String? = null,
    val country: String? = BR,
    val purposeAddress: List<PurposeAddressRequest>? = null
)