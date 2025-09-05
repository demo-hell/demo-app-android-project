package br.com.mobicare.cielo.commons.domains.entities

data class PasswordError(
        val type: String,
        val errorCode: String,
        val value: String,
        val errorMessage: String
)