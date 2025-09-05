package br.com.mobicare.cielo.adicaoEc.domain.model

data class BankAccountObj(
    val merchantId: String,
    val bankAccount: BankAccount
)

data class BankAccount(
    val account: String,
    var accountType: String,
    val agency: String,
    val code: String
)