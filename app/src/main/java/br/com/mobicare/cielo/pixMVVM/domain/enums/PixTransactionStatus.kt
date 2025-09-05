package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixTransactionStatus {
    NOT_EXECUTED,
    EXECUTED,
    PENDING,
    PROCESSING,
    SCHEDULED,
    SCHEDULED_EXECUTED,
    CANCELLED,
    FAILED,
    REVERSAL_EXECUTED,
    SENT_WITH_ERROR;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}