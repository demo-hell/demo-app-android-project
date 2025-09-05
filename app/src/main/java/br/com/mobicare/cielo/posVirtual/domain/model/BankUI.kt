package br.com.mobicare.cielo.posVirtual.domain.model

data class BankUI(
    val id: Int,
    val code: String?,
    var isSelected: Boolean = false,
    val name: String,
    val account: String,
    val agency: String,
    val onlyAgency: String?
)
