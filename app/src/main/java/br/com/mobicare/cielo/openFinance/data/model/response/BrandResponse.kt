package br.com.mobicare.cielo.openFinance.data.model.response

data class BrandResponse(
    val brand: String,
    val institutions: List<InstitutionResponse>?
)

data class InstitutionResponse(
    val organizationId: String?,
    val organizationName: String,
    val logoUri: String?,
    val authorizationServerId: String?,
    val brandDescription: String?
)