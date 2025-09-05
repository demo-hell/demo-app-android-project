package br.com.mobicare.cielo.openFinance.data.model.request

data class CreateShareRequest(
        val authorizationServerId: String,
        val organizationId: String,
        val finalityId: String?
)
