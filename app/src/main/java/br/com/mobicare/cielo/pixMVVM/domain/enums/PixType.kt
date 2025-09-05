package br.com.mobicare.cielo.pixMVVM.domain.enums

enum class PixType {
    TRANSFER_DEBIT,
    TRANSFER_CREDIT,
    QRCODE_DEBIT,
    QRCODE_CREDIT,
    WITHDRAWAL_DEBIT,
    WITHDRAWAL_CREDIT,
    CHANGE_DEBIT,
    CHANGE_CREDIT,
    REVERSAL_DEBIT,
    REVERSAL_CREDIT,
    SCHEDULE_DEBIT,
    FEE_DEBIT,
    INIT_PAY_DEBIT,
    INIT_PAY_CREDIT,
    SCHEDULE_RECURRENCE_DEBIT,
    ;

    companion object {
        fun find(name: String?) = values().firstOrNull { it.name == name }
    }
}
