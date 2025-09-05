package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import androidx.annotation.Keep

@Keep
data class Bank(
        val code: String?,
        val name: String?
)