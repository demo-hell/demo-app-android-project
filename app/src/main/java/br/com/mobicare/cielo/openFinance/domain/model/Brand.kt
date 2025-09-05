package br.com.mobicare.cielo.openFinance.domain.model

data class Brand(
    val brand: String,
    val institutions: List<Institution>?
)

data class Institution(
    val organizationId: String?,
    val organizationName: String,
    val logoUri: String?,
    val authorizationServerId: String?,
    val brandDescription: String?
)