package br.com.mobicare.cielo.pix.enums

import androidx.annotation.Keep

@Keep
enum class BankAccountTypeEnum(val key: String) {
    CURRENT_ACCOUNT("CC"),
    SAVINGS_ACCOUNT("SA"),
    PAYMENT_ACCOUNT("PA");

    companion object {
        fun acronymToName(acronym: String?): String {
            return when (acronym) {
                CURRENT_ACCOUNT.key -> "Conta corrente"
                SAVINGS_ACCOUNT.key -> "Conta poupança"
                PAYMENT_ACCOUNT.key -> "Conta pagamento"
                else -> ""
            }
        }
    }
}