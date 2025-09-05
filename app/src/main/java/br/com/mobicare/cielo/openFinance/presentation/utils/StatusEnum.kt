package br.com.mobicare.cielo.openFinance.presentation.utils

import br.com.mobicare.cielo.R

enum class StatusEnum(
    val status: Int,
    val background: Int,
    val icon: Int,
    val desc: Int,
    val textStyle: Int
) {
    ACTIVE(
        R.string.active_opf,
        R.drawable.background_green800_radius_8,
        R.drawable.ic_check_round_green,
        R.string.expires_in_opf,
        R.style.bold_montserrat_14_green_800
    ),
    TEMPORARILY_UNAVAILABLE(
        R.string.temporarily_unavailable_opf,
        R.drawable.background_cloud800_radius_8,
        R.drawable.ic_clock_black,
        R.string.expires_in_opf,
        R.style.bold_montserrat_14_contrast_1000_spacing_3
    ),
    PENDING_AUTHORIZATION(
        R.string.pending_authorization_opf,
        R.drawable.background_brand800_radius_8,
        R.drawable.ic_info_blue,
        R.string.expires_in_opf,
        R.style.bold_montserrat_14_brand_800
    ),
    CLOSED(
        R.string.closed_opf,
        R.drawable.background_danger400_radius_8,
        R.drawable.ic_close_round_red,
        R.string.closed_in_opf,
        R.style.bold_montserrat_14_red_700
    ),
    EXPIRED(
        R.string.expired_opf,
        R.drawable.background_danger400_radius_8,
        R.drawable.ic_calendar_clock_red,
        R.string.expired_in_opf,
        R.style.bold_montserrat_14_red_700
    )
}