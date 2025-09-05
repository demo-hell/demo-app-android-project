package br.com.mobicare.cielo.accessManager.domain.model

data class CustomProfiles(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val global: Boolean = false,
    val status: String? = null,
    val resources: List<CustomProfileResources>? = null,
)
