package br.com.mobicare.cielo.turboRegistration.data.model.request

import br.com.cielo.libflue.util.ZERO

data class PurposeAddressRequest(
    val type: Int = ZERO,
    val contact: Int? = null,
    val name: String? = null
)