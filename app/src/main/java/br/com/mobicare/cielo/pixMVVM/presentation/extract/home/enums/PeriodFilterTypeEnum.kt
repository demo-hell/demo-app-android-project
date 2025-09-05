package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PeriodFilterTypeEnum(@StringRes val label: Int) {
    SEVEN_DAYS(R.string.pix_extract_filter_item_period_label_last_seven_days),
    FIFTEEN_DAYS(R.string.pix_extract_filter_item_period_label_last_fifteen_days),
    THIRTY_DAYS(R.string.pix_extract_filter_item_period_label_last_thirty_days);

    val isSelected get() = this != THIRTY_DAYS
}