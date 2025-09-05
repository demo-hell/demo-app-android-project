package br.com.mobicare.cielo.meusCartoes.clients.api.domain

data class CardActivationCatenoRequest(
        val password: String,
        val passwordConfirmation: String
)
