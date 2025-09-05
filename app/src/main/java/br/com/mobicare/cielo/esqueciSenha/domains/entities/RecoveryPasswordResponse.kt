package br.com.mobicare.cielo.esqueciSenha.domains.entities

data class RecoveryPasswordResponse(
        val tokenExpirationInMinutes: Int,
        val email: String,
)