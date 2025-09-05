package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class StatusFilterTypeEnum(@StringRes val label: Int) {
    ALL_STATUS(R.string.pix_extract_filter_item_status_label_all_status),
    SCHEDULED(R.string.pix_extract_filter_item_status_label_only_scheduled),
    CANCELLED(R.string.pix_extract_filter_item_status_label_only_scheduled_canceled),
    FAILED(R.string.pix_extract_filter_item_status_label_only_scheduled_failed);

    val isSelected get() = this != ALL_STATUS
}