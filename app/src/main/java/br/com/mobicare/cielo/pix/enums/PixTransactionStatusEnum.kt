package br.com.mobicare.cielo.pix.enums

enum class PixTransactionStatusEnum {
    NOT_EXECUTED,
    EXECUTED,
    PENDING,
    PROCESSING,
    SCHEDULED,
    SCHEDULED_EXECUTED,
    CANCELLED,
    FAILED,
    REVERSAL_EXECUTED,
    SENT_WITH_ERROR
}