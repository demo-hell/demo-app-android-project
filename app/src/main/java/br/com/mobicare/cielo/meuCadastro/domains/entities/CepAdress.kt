package br.com.mobicare.cielo.meuCadastro.domains.entities

data class CepAdress(
        val address: String,
        val addressCode: String,
        val addressType: String,
        val city: String,
        val country: String,
        val neighborhood: String,
        val state: String
)