package br.com.mobicare.cielo.suporteTecnico.data

data class Problems(
    val name: String,
    val issueCode: Int,
    val subProblem: List<String>
)

data class SubOptions(
    val title: String
)


