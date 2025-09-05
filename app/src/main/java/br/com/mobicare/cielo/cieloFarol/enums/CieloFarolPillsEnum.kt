package br.com.mobicare.cielo.cieloFarol.enums

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.hourMinuteToGenericFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString


enum class CieloFarolPillsEnum(
    @StringRes val title: Int,
    @ColorRes val textColor: Int,
    @ColorRes val backgroundColor: Int,
    @DrawableRes val icon: Int
) {
    BEST_WEEK_DAY(
        title = R.string.cielo_farol_best_day_of_week,
        textColor = R.color.ocean_600,
        backgroundColor = R.color.ocean_100,
        icon = R.drawable.ic_date_time_calendar_cloud_300_16_dp
    ),
    BEST_TIME(
        title = R.string.cielo_farol_best_time,
        textColor = R.color.sunshine_600,
        backgroundColor = R.color.sunshine_100,
        icon = R.drawable.ic_date_time_clock_alert_500_16_dp
    ),
    AVERAGE_VALUE(
        title = R.string.cielo_farol_average_consume,
        textColor = R.color.aurora_600,
        backgroundColor = R.color.aurora_100,
        icon = R.drawable.ic_chart_line_black_20dp
    );

    fun formatValue(value: String): String {
        return when(this) {
            AVERAGE_VALUE -> value.toDouble().toPtBrRealString()
            BEST_TIME -> value.hourMinuteToGenericFormat() ?: EMPTY
            else -> value
        }
    }
}