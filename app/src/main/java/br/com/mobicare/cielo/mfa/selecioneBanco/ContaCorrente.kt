package br.com.mobicare.cielo.mfa.selecioneBanco

data class ContaCorrente(
    val conta: String,
    val agência: String,
    val banco: Banco
)

data class Banco(
        val numero: String,
        val nome: String,
        val bandeira: String
)