package br.com.mobicare.cielo.pix.domain

data class PixAddNewTrustedDestinationResponse(
    val merchantNumber: String? = null,
    val requests: List<Request>? = null,
    val serviceGroup: String? = null
)

data class Request(
    val requestDate: String? = null,
    val status: String? = null,
    val type: String? = null
)