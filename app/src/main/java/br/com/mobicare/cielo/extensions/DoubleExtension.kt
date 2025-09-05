package br.com.mobicare.cielo.extensions

import br.com.cielo.libflue.util.DOUBLE_ZERO_VALUE
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.pix.constants.EMPTY

fun Double.getPercent(): String {
    return "${this.toPtBrRealStringWithoutSymbol()}%"
}

fun Double?.toStringOrEmpty(): String {
    return this?.toString() ?: EMPTY
}

fun Double?.isHigherThanZero(): Boolean {
    return this != null && this > ZERO_DOUBLE
}
