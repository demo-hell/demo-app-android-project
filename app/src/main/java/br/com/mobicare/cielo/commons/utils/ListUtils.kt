package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.ONE

object ListUtils {
    const val SEPARATOR_COMMA = ", "
    const val SEPARATOR_AND = " e "
}

fun List<String>.joinWithComma() = joinToString(ListUtils.SEPARATOR_COMMA)

fun List<String>.joinWithLastCustomSeparator(
    separator: String = ListUtils.SEPARATOR_COMMA,
    lastSeparator: String = ListUtils.SEPARATOR_AND,
) = if (size > ONE) {
    "${dropLast(ONE).joinToString(separator)}$lastSeparator${last()}"
} else {
    joinToString(separator)
}