package br.com.mobicare.cielo.pixMVVM.presentation.transfer.enums

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class PixPeriodRecurrence(
    @StringRes val label: Int,
) {
    WEEKLY(R.string.pix_transfer_recurrence_label_weekly_period),

    // TODO: REMOVER COMENTÁRIO QUANDO FOR ADICIONADO NA API (Descomentar no arquivo PixTransferRecurrenceFragment.kt)
    // BIWEEKLY(R.string.pix_transfer_recurrence_label_biweekly_period),
    MONTHLY(R.string.pix_transfer_recurrence_label_monthly_period),
}
