package br.com.mobicare.cielo.migration.domain

data class MigrationRequest(
    val fullName : String,
    val cpf : String?,
    val email : String,
    val currentPassword : String,
    val password : String,
    val passwordConfirmation : String,
    val merchantId : String
)