package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixDocumentType(val key: String) {
    CPF("F"),
    CNPJ("J");

    companion object {
        fun find(key: String?) = values().firstOrNull { it.key == key }
    }
}