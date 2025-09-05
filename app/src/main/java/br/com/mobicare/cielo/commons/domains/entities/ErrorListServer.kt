package br.com.mobicare.cielo.commons.domains.entities

data class ErrorListServer(
        val errorCode: String,
        val errorMessage: String,
        val value: String,
        val type: String
)