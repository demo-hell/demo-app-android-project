package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixOwnerType(
    val documentType: String,
) {
    NATURAL_PERSON("CPF"),
    LEGAL_PERSON("CNPJ"),
    ;

    companion object {
        fun find(entry: String) = values().firstOrNull { it.name == entry }
    }
}
