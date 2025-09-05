package br.com.mobicare.cielo.arv.presentation.model

data class ScheduleContract(
    val cnpj: String,
    val schedule: String,
    val fee: String,
    val hireDate: String
)
