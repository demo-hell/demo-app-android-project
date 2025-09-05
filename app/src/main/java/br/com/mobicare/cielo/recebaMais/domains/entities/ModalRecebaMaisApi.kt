package br.com.mobicare.cielo.recebaMais.domains.entities

/**
 * create by Enzo Teles
 * */

data class ModalRecebaMaisResponse(
    val offers: List<Offer>
)

data class Offer(
        val customerId: String,
        val customerType: String,
        val description: String,
        val endDatePaymentFirstInstallment: String,
        val id: String,
        val installmentAmount: Int,
        val installments: Int,
        val loanLimit: Int,
        val monthlyInterestRate: Int,
        val name: String,
        val partner: Partner,
        val startDatePaymentFirstInstallment: String,
        val steps: List<String>
)

data class Partner(
    val merchantId: String,
    val name: String
)

data class EligibilidadeResponse(
        val code: Int,
        val name: String
)