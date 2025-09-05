package br.com.mobicare.cielo.pix.enums

enum class PixOwnerTypeEnum(val owner: String) {
    NATURAL_PERSON("CPF"),
    LEGAL_PERSON("CNPJ")
}