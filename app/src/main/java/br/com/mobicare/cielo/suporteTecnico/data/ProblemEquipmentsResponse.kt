package br.com.mobicare.cielo.suporteTecnico.data

data class ProblemEquipments(
val code: String,
val description: String,
val options: List<Option>?,
)

data class Option(
    val code: String,
    val description: String,
)