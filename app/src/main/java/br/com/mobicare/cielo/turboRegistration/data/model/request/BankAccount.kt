package br.com.mobicare.cielo.turboRegistration.data.model.request

data class BankAccount(
    val account: String,
    val accountDigit: String,
    val agency: String,
    val agencyDigit: String,
    val code: Int,
    val savingsAccount: Boolean
)