package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixTransactionType {
    TRANSFER_CREDIT, TRANSFER_DEBIT;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}