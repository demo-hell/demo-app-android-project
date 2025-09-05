package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class TransactionFilterTypeEnum(@StringRes val label: Int) {
    ALL_TRANSACTIONS(R.string.pix_extract_filter_item_transactions_label_all_transactions),
    TRANSFER(R.string.pix_extract_filter_item_transactions_label_only_transfers),
    QRCODE(R.string.pix_extract_filter_item_transactions_label_only_qr_codes);

    val isSelected get() = this != ALL_TRANSACTIONS
}