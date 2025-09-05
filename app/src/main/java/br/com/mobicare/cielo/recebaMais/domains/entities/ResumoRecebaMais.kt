package br.com.mobicare.cielo.recebaMais.domains.entities

data class ResumoResponse(
        val contracts: List<Contract>
)

data class Contract(
        val annualEffectiveCostRate: Double,
        val annualInterestRate: Double,
        val boletoAmount: Int,
        val contractDate: String,
        val customerId: String,
        val debitAmount: Double,
        val id: String,
        val installmentAmount: Double,
        val installments: Int,
        val insuranceAmount: Int,
        val iof: Double,
        val iofRate: Double,
        val monthlyEffectiveCostRate: Double,
        val monthlyInterestRate: Double,
        val partner: Partner1,
        val paymentFirstInstallmentDate: String,
        val pid: Pid1,
        val registrationFee: Double,
        val requestedAmount: Double,
        val status: String
)

data class BankAccount1(
        val account: String,
        val accountDigit: String,
        val agency: String,
        val code: Int,
        val name: String
)

data class Pid1(
        val bankAccount: BankAccount1,
        val image: String
)

data class Partner1(
        val name: String
)