package br.com.mobicare.cielo.accessManager.model

data class AccessManagerAssignRoleRequest(
    val userId: String,
    val role: String
)
