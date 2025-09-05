package br.com.mobicare.cielo.extensions

import android.content.Context
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.NINE
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.enums.DiasDaSemana
import br.com.mobicare.cielo.pix.constants.EMPTY

fun Int.toStringAsDaysTo(): String {
    return when {
        this == 0 -> {
            Text.SAME_DAY
        }
        this in 2..30 -> {
            "${Text.IN} $this ${Text.DAYS_MAPPER}"
        }
        else -> {
            "${Text.IN} $this ${Text.DAY_MAPPER}"

        }
    }
}

fun Int.toStringAsDays(context: Context? = null): String {
    val c = context ?: CieloApplication.context!!
    return when {
        this == 0 -> {
            c.getString(R.string.label_zero_day)
        }
        this in 2..30 -> {
            c.getString(R.string.label_plural_days, this)
        }
        else -> {
            c.getString(R.string.label_single_day, this)
        }
    }
}

fun Int.toStringAsDaysWithPlusSign(): String {
    return when {
        this == 0 -> {
            Text.SAME_DAY

        }
        this in 2..30 -> {
            "$this ${Text.DAYS}"
        }
        this > 30 -> {
            "+$this ${Text.DAYS}"

        }
        else -> {
            "$this ${Text.DAY}"
        }
    }
}

fun Int.toStringReceivableDayWeek(context: Context? = null): String {
    val dayMap = mapOf(
            0 to DiasDaSemana.DOMINGO.dia,
            1 to DiasDaSemana.SEGUNDA.dia,
            2 to DiasDaSemana.TERCA.dia,
            3 to DiasDaSemana.QUARTA.dia,
            4 to DiasDaSemana.QUINTA.dia,
            5 to DiasDaSemana.SEXTA.dia,
            6 to DiasDaSemana.SABADO.dia,
    )
    return dayMap[this] ?: "-"
}

fun Int?.addDoubleZeroPrefix(): String =
        this?.toString()?.padStart(2, '0') ?: ""

val Int?.orZero: Int
    get() = this ?: 0

fun Int?.toStringOrEmpty(): String {
    return this?.toString() ?: return EMPTY
}

fun Int.toNineDigitString(): String {
    return this.toString().padStart(NINE, '0')
}
