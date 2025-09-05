package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.scheduleCancel

import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

class PixCancelScheduleContentFactory(private val isRecurrentTransferSchedule: Boolean) {
    val bottomSheetContent get() =
        if (isRecurrentTransferSchedule) {
            BottomSheetContent(
                title = R.string.pix_extract_detail_title_bs_confirm_cancel_recurrence,
                message = R.string.pix_extract_detail_message_bs_confirm_cancel_recurrence,
                confirmButtonText = R.string.pix_extract_detail_label_button_bs_confirm_cancel_recurrence,
                cancelButtonText = R.string.pix_extract_detail_label_button_bs_keep_recurrence,
            )
        } else {
            BottomSheetContent(
                title = R.string.pix_extract_detail_title_bs_confirm_cancel_schedule,
                message = R.string.pix_extract_detail_message_bs_confirm_cancel_schedule,
                confirmButtonText = R.string.pix_extract_detail_label_button_bs_confirm_cancel_schedule,
                cancelButtonText = R.string.back,
            )
        }

    val scheduleDetailSuccessContent get() =
        if (isRecurrentTransferSchedule) {
            ScheduleDetailSuccessContent(
                title = R.string.pix_extract_detail_title_bs_success_cancel_recurrence,
                message = R.string.pix_extract_detail_message_bs_success_cancel_recurrence,
            )
        } else {
            ScheduleDetailSuccessContent(
                title = R.string.pix_extract_detail_title_bs_success_cancel_schedule,
                message = R.string.pix_extract_detail_message_bs_success_cancel_schedule,
            )
        }

    data class BottomSheetContent(
        @StringRes val title: Int,
        @StringRes val message: Int,
        @StringRes val confirmButtonText: Int,
        @StringRes val cancelButtonText: Int,
    )

    data class ScheduleDetailSuccessContent(
        @StringRes val title: Int,
        @StringRes val message: Int,
    )
}
