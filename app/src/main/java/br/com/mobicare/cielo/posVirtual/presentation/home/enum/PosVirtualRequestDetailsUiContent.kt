package br.com.mobicare.cielo.posVirtual.presentation.home.enum

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus

enum class PosVirtualRequestDetailsUiContent(
    val productStatus: PosVirtualStatus,
    @StringRes val statusText: Int,
    @DrawableRes val statusIcon: Int
) {

    PENDING(
        PosVirtualStatus.PENDING,
        R.string.pos_virtual_request_details_status_pending,
        R.drawable.ic_date_time_clock_alert_500_16_dp
    ),

    CANCELED(
        PosVirtualStatus.CANCELED,
        R.string.pos_virtual_request_details_status_canceled,
        R.drawable.ic_symbol_close_round_danger_500_16_dp
    ),

    FAILED(
        PosVirtualStatus.FAILED,
        R.string.pos_virtual_request_details_status_failed,
        R.drawable.ic_symbol_alert_round_warning_16_dp
    );

    companion object {
        fun find(status: PosVirtualStatus?) = values().firstOrNull { it.productStatus == status } ?: FAILED
    }

}