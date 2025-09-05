package br.com.mobicare.cielo.recebaMais.domains.entities

data class ContratarEmprestimoRecebaMaisRequest(
        val email: String,
        val phoneNumber: String,
        val offerId: String,
        val pid: Pid
)

data class Pid(
        val bankAccount: BankAccount
)


data class BankAccount(
        val account: String,
        val accountDigit: String,
        val agency: String,
        val code: Int
)

data class ContratacaoResponse(
        val status: String
)