package br.com.mobicare.cielo.eventTracking.domain.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.mobicare.cielo.R

enum class EventRequestStatus(
    @DrawableRes val statusIcon: Int,
    @ColorRes val statusIconTint: Int,
    @ColorRes val statusBackgroundColor: Int,
    @StringRes val statusText: Int
) {
    WITHOUT_UPDATE(
        statusIcon = R.drawable.ic_cielo_status_without_update,
        statusIconTint = R.color.machine_status_without_update,
        statusBackgroundColor = R.color.machine_status_without_update_bg,
        statusText = R.string.flex_tag_without_update
    ),
    IN_TRANSIT(
        statusIcon = R.drawable.ic_cielo_status_in_transit,
        statusIconTint = R.color.machine_status_in_transit,
        statusBackgroundColor = R.color.machine_status_in_transit_bg,
        statusText = R.string.flex_tag_in_transit
    ),
    ATTENDED(
        statusIcon = R.drawable.ic_cielo_status_attended,
        statusIconTint = R.color.machine_status_attended,
        statusBackgroundColor = R.color.machine_status_attended_bg,
        statusText = R.string.flex_tag_attended
    ),
    UNREALIZED(
        statusIcon = R.drawable.ic_cielo_status_unrealized,
        statusIconTint = R.color.machine_status_unrealized,
        statusBackgroundColor = R.color.machine_status_unrealized_bg,
        statusText = R.string.flex_tag_unrealized
    ),
    RESCHEDULED(
        statusIcon = R.drawable.ic_cielo_status_rescheduled,
        statusIconTint = R.color.machine_status_rescheduled,
        statusBackgroundColor = R.color.machine_status_rescheduled_bg,
        statusText = R.string.flex_tag_rescheduled
    ),
    IN_RESOLUTION(
        statusIcon = R.drawable.ic_cielo_status_in_resolution,
        statusIconTint = R.color.machine_status_in_resolution,
        statusBackgroundColor = R.color.machine_status_in_resolution_bg,
        statusText = R.string.flex_tag_in_resolution
    ),
}