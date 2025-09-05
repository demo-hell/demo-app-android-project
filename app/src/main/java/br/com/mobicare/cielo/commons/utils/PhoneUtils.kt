package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.ELEVEN
import br.com.mobicare.cielo.commons.constants.TWELVE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY

fun String.phone(): String {
    val phone = this
        .replace("(", "")
        .replace(")", "")
        .replace("-", "")
        .replace(" ", "")
        .replace("+", "")
    if (phone.length < 10 || phone.length > 11) {
        return ""
    }
    return if (phone.length == 10) {
        String.format(
            "(%s) %s-%s",
            phone.substring(0, 2),
            phone.substring(2, 6),
            phone.substring(6, phone.length)
        )
    } else {
        String.format(
            "(%s) %s-%s",
            phone.substring(0, 2),
            phone.substring(2, 7),
            phone.substring(7, phone.length)
        )
    }
}

fun String.phoneNumber(): String {
    val phone = this
        .replace("(", "")
        .replace(")", "")
        .replace("-", "")
        .replace(" ", "")
        .replace("+", "")
    return phone
}

fun String.getNumber(): String = this.replace("[^0-9]".toRegex(), EMPTY)

fun String.getAreaCode(): String = if (this.length == ELEVEN) this.substring(ZERO, TWO) else EMPTY

fun String.getNumberPhone(): String = if (this.length == ELEVEN) this.substring(TWO) else EMPTY