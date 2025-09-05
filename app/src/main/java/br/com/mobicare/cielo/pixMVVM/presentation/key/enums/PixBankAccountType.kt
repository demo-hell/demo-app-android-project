package br.com.mobicare.cielo.pixMVVM.presentation.key.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PixBankAccountType(val key: String, @StringRes val nameRes: Int) {
    CHECKING_ACCOUNT("CC", R.string.pix_key_bank_account_type_checking_account),
    SAVINGS_ACCOUNT("SA", R.string.pix_key_bank_account_type_savings_account),
    PAYMENT_ACCOUNT("PA", R.string.pix_key_bank_account_type_payment_account);

    companion object {
        fun findByKey(key: String) = values().firstOrNull { it.key == key }
    }
}