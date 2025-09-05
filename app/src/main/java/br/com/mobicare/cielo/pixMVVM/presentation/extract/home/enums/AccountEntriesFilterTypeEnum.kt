package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class AccountEntriesFilterTypeEnum(@StringRes val label: Int) {
    ALL_ACCOUNT_ENTRIES(R.string.pix_extract_filter_item_accounting_entries_label_all_transactions),
    CREDIT(R.string.pix_extract_filter_item_accounting_entries_label_only_entries),
    DEBIT(R.string.pix_extract_filter_item_accounting_entries_label_only_withdrawal);

    val isSelected get() = this != ALL_ACCOUNT_ENTRIES
}