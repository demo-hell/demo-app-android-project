package br.com.mobicare.cielo.recebaMais.domains.entities


/**
 * create by Enzo Teles
 * */
data class SimulationResponse(
        val installment: Installment,
        val token: String
)

data class Installment(
    val amount: Int,
    val installmentAmount: Double,
    val installments: Int,
    val iof: Double,
    val iofRate: Double,
    val monthlyInterestRate: Double,
    val totalAmount: Double
)