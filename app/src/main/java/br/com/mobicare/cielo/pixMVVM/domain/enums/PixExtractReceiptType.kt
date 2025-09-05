package br.com.mobicare.cielo.pixMVVM.domain.enums

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics

enum class PixExtractReceiptType(
    @StringRes val title: Int,
    val isReceipt: Boolean,
    @DrawableRes val icon: Int,
    @ColorRes val iconColor: Int,
    @ColorRes val backgroundIconColor: Int,
) {
    CHANGE_CREDIT(
        R.string.pix_extract_title_card_change_credit,
        true,
        R.drawable.ic_payments_qr_code_neutral_soft_19_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    CHANGE_DEBIT(
        R.string.pix_extract_title_card_change_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    INIT_PAY_CREDIT(
        R.string.pix_extract_title_card_init_pay_credit,
        true,
        R.drawable.ic_money_note_arrow_down_neutral_soft_20_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    INIT_PAY_DEBIT(
        R.string.pix_extract_title_card_fee_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    QRCODE_CREDIT(
        R.string.pix_extract_title_card_qrcode_credit,
        true,
        R.drawable.ic_payments_qr_code_neutral_soft_19_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    QRCODE_DEBIT(
        R.string.pix_extract_title_card_qrcode_debit,
        false,
        R.drawable.ic_payments_qr_code_neutral_soft_19_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    REVERSAL_DEBIT(
        R.string.pix_extract_title_card_reversal_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    REVERSAL_CREDIT(
        R.string.pix_extract_title_card_reversal_credit,
        true,
        R.drawable.ic_money_note_arrow_down_neutral_soft_20_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    SCHEDULE_DEBIT(
        R.string.pix_extract_title_card_schedule,
        false,
        R.drawable.ic_date_time_clock_neutral_soft_20_dp,
        R.color.feedback_warning_dark,
        R.color.surface_warning,
    ),
    SCHEDULE_RECURRENCE_DEBIT(
        R.string.pix_extract_title_card_recurrence_schedule,
        false,
        R.drawable.ic_date_time_calendar_clock_neutral_soft_20_dp,
        R.color.feedback_warning_dark,
        R.color.surface_warning,
    ),
    TRANSFER_CREDIT(
        R.string.pix_extract_title_card_transfer_credit,
        true,
        R.drawable.ic_money_note_arrow_down_neutral_soft_20_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    TRANSFER_DEBIT(
        R.string.pix_extract_title_card_transfer_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    WITHDRAWAL_CREDIT(
        R.string.pix_extract_title_card_withdrawal_credit,
        true,
        R.drawable.ic_payments_qr_code_neutral_soft_19_dp,
        R.color.data_viz_chart_4,
        R.color.surface_system,
    ),
    WITHDRAWAL_DEBIT(
        R.string.pix_extract_title_card_withdrawal_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    FEE_DEBIT(
        R.string.pix_extract_title_card_fee_debit,
        false,
        R.drawable.ic_money_note_arrow_up_neutral_soft_20_dp,
        R.color.feedback_danger_main,
        R.color.surface_danger,
    ),
    ;

    companion object {
        fun parsePixExtractReceiptType(value: String?): PixExtractReceiptType? =
            try {
                value?.let {
                    PixExtractReceiptType.valueOf(value)
                }
            } catch (e: Exception) {
                e.message?.logFirebaseCrashlytics()
                null
            }
    }
}
