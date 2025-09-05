package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixExtractType {
    TRANSFER_DEBIT,
    TRANSFER_CREDIT,
    SCHEDULE_DEBIT,
    REVERSAL_DEBIT,
    REVERSAL_CREDIT;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}