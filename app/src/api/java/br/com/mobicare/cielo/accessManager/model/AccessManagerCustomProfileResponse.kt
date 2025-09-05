package br.com.mobicare.cielo.accessManager.model

import com.google.errorprone.annotations.Keep

@Keep
data class AccessManagerCustomProfileResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val global: Boolean?,
    val status: String?,
    val resources: List<CustomProfileResources>? = null,
)

@Keep
data class CustomProfileResources(
    val resourceId: String?,
    val resourceName: String?,
    val accessTypes: List<String?>
)