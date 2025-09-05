package br.com.mobicare.cielo.accessManager.data.model.response

data class GetCustomActiveProfilesResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val global: Boolean = false,
    val status: String?,
    val resources: List<CustomProfileResourcesResponse>? = null,
)

data class CustomProfileResourcesResponse(
    val resourceId: String?,
    val resourceName: String?,
    val accessTypes: List<String?>?
)