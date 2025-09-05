package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class LoanSimulationResponse(
        @SerializedName("installments")
        val installments: List<Installment>
)