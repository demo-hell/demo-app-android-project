package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import kotlin.math.abs

fun Double?.orZero() = this ?: ZERO_DOUBLE

fun Int?.orZero() = this ?: ZERO

fun Double?.asNegative() = this?.let { -abs(it) }