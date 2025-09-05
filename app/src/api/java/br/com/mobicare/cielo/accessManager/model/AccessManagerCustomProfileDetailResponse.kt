package br.com.mobicare.cielo.accessManager.model

import com.google.errorprone.annotations.Keep

@Keep
data class AccessManagerCustomProfileDetailResponse(
    val id: String?,
    val name: String?,
    val description: String?,
    val global: Boolean?,
    val status: String?,
    val resources: List<CustomProfileDetailResources>? = null,
    val roles: List<String?>
)

@Keep
data class CustomProfileDetailResources(
    val resourceId: String?,
    val resourceName: String?,
    val accessTypes: List<String?>
)