package br.com.mobicare.cielo.commons.utils

import br.com.cielo.libflue.util.EMPTY
import br.com.concrete.canarinho.formatador.Formatador
import br.com.mobicare.cielo.commons.constants.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun Double.toPtBrRealString(isPrefix: Boolean = true): String {
    val ptBrFormat = this.toPtBrRealStringWithoutSymbol()
    return if (isPrefix) "R$ $ptBrFormat" else ptBrFormat
}

fun BigDecimal.toPtBrRealString(): String {
    val ptBrFormat = this.toPtBrRealStringWithoutSymbol()
    return "R$ $ptBrFormat"
}

fun Double.toPtBrWithNegativeRealString(): String {
    val isNegative = this < 0.0f
    var tempValue: Double = this
    if (isNegative) {
        tempValue *= -1.0f
    }
    var text = tempValue.toPtBrRealString()
    if (isNegative) {
        text = "- $text"
    }
    return text
}

fun Double.toPtBrRealStringWithoutSymbol(): String {
    val ptBrFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            as DecimalFormat
    ptBrFormat.minimumFractionDigits = 2
    val decimalFormatSymbols = ptBrFormat.decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = ""

    ptBrFormat.decimalFormatSymbols = decimalFormatSymbols

    return ptBrFormat.format(this).trim()
}

fun BigDecimal.toPtBrRealStringWithoutSymbol(): String {
    val ptBrFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            as DecimalFormat
    ptBrFormat.minimumFractionDigits = 2
    val decimalFormatSymbols = ptBrFormat.decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = ""

    ptBrFormat.decimalFormatSymbols = decimalFormatSymbols

    return ptBrFormat.format(this).trim()
}

fun Double.toPtBrRealStringWithoutSymbol3House(): String {
    val ptBrFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            as DecimalFormat
    ptBrFormat.minimumFractionDigits = 3
    val decimalFormatSymbols = ptBrFormat.decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = ""

    ptBrFormat.decimalFormatSymbols = decimalFormatSymbols

    return ptBrFormat.format(this).trim()
}

fun Double.toPtBrRealStringWithoutSymbol1(): String {
    val ptBrFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            as DecimalFormat
    ptBrFormat.minimumFractionDigits = 1
    val decimalFormatSymbols = ptBrFormat.decimalFormatSymbols
    decimalFormatSymbols.currencySymbol = ""

    ptBrFormat.decimalFormatSymbols = decimalFormatSymbols

    return ptBrFormat.format(this).trim()
}


fun String.moneyToBigDecimalValue(): BigDecimal {
    val valueDouble = this.replace("[^0-9]".toRegex(), "").toLong() / 100.0
    try {
        return BigDecimal.valueOf(valueDouble)
    } catch (ex: NumberFormatException) {
        throw IllegalArgumentException()
    }
}

fun String?.moneyToDoubleValue(): Double {
    return try {
        if (this.isNullOrEmpty()) return ZERO_DOUBLE
        val value = this.replace(DOT, EMPTY).replace(COMMA, DOT).replace("[^\\d.]".toRegex(), EMPTY)
        if (value.isNotEmpty())
            BigDecimal(value).setScale(TWO, RoundingMode.HALF_DOWN).toDouble()
        else ZERO_DOUBLE
    } catch (ex: NumberFormatException) {
        ZERO_DOUBLE
    }
}

fun String.moneyToDouble(): Double {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    format.minimumFractionDigits = 2

    try {
        return format.parse(this)?.toDouble() ?: ZERO_DOUBLE
    } catch (ex: NumberFormatException) {
        throw IllegalArgumentException()
    }
}

fun String.currencyToDouble(): Double {
    return BigDecimal
        .valueOf(convertStringToDouble())
        .divide(BigDecimal(100))
        .setScale(2, RoundingMode.HALF_DOWN).toDouble()
}

fun String.textToMoneyBigDecimalFormat(): BigDecimal {

    return BigDecimal
        .valueOf(convertStringToDouble())
        .divide(BigDecimal(100))
        .setScale(2, RoundingMode.HALF_DOWN)
}

fun Double?.roundToOneDecimal() = String.format("%.1f", this?.toFloat())
    .replace(".0", "")
    .replace(",0", "")

fun Float?.roundToTwoDecimal() = String.format("%.2f", this)
    .replace(".", ",")

fun Float?.roundToThreeDecimal() = String.format("%.3f", this)
    .replace(".", ",")

fun Double?.roundToTwoDecimal() = String.format("%.2f", this)

private fun String.convertStringToDouble(): Double {
    var onlyNumberDoubleValue = 0.0

    val onlyNumbersValue = Formatador.Padroes.PADRAO_SOMENTE_NUMEROS
        .matcher(this)
        .replaceAll("")

    if (onlyNumbersValue.isNotEmpty()) {
        onlyNumberDoubleValue = onlyNumbersValue.toDouble()
    }
    return onlyNumberDoubleValue
}

fun Double.decimalMask(mask: String = "##"): String {
    val decimalFormat = DecimalFormat("0.$mask")
    return decimalFormat.format(this)
}

fun BigDecimal.toCents() = this.multiply(BigDecimal(ONE_HUNDRED)).longValueExact()
