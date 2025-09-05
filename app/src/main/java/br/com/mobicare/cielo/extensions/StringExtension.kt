package br.com.mobicare.cielo.extensions

import android.util.Patterns
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import br.com.cielo.libflue.util.ELEVEN
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.BR
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.DOT
import br.com.mobicare.cielo.commons.constants.ERROR_NOT_BOOTING
import br.com.mobicare.cielo.commons.constants.PT
import br.com.mobicare.cielo.commons.constants.RATE_FORMAT
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.helpers.FormHelper
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_INITIAL
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import br.com.mobicare.cielo.commons.utils.LANDLINE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.LONG_TIME
import br.com.mobicare.cielo.commons.utils.PHONE_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import java.text.Normalizer
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val COLON_CHARACTER = ":"
private const val INVALID = -1
private const val TIME_DIGIT = 3
private const val START = 0

fun Double.configExpectedDeposited(summary: Summary): String {
    if (summary.paidAmount != null) {
        return String.format("", R.string.meus_recebimentos_depositado)
    } else if (summary.expectedAmount != null) {
        return String.format("", R.string.meus_recebimentos_previsto)
    }
    return ""
}

fun String.getFirstName() = this.substringBefore(" ", "")

private fun getAsDays(day: Int): String {
    return if (day > 1) {
        "$day dias"
    } else {
        "$day dia"
    }
}

private fun getPercent(percent: Double): String {
    return "${percent.toPtBrRealStringWithoutSymbol()}%"
}

fun String?.capitalizeWords(): String? =
    this?.split(" ")?.joinToString(" ") { it.toLowerCasePTBR().capitalizePTBR() }?.trim()

fun CharSequence?.capitalizeWords(): String? =
    this?.toString()?.capitalizeWords()

fun String?.formatBankName(): String? =
    this?.capitalizeWords()?.replace("S.a", "S.A")?.replace("S/a", "S/A")

fun String?.verifyNullOrBlankValue(): String? = if (this.isNullOrBlank()) null else this

private fun localePTBR() = Locale("pt", "BR")

fun String?.toLowerCasePTBR(): String =
    this?.toLowerCase(localePTBR()) ?: ""

fun CharSequence?.toLowerCasePTBR(): String? =
    this?.toString()?.toLowerCasePTBR()

fun String?.capitalizePTBR(): String =
    this?.capitalize(localePTBR()) ?: ""

fun CharSequence?.capitalizePTBR(): String? =
    this?.toString()?.capitalizePTBR()

fun CharSequence?.removeAccents(): String {
    return this?.let {
        val temp = Normalizer.normalize(it, Normalizer.Form.NFD)
        "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
    } ?: ""
}

fun String.formatterDate(
    mask: String = LONG_TIME,
    resultMask: String = SIMPLE_DT_FORMAT_MASK
): String {
    return try {
        val currentLocalDt = DateTimeFormatter
            .ofPattern(mask)
            .parse(this)
        DateTimeFormatter.ofPattern(resultMask).format(currentLocalDt)
    } catch (ex: Exception) {
        this
    }
}

fun String.clearDate(): String {
    val date = this
    val lastIndex = date.lastIndexOf(COLON_CHARACTER)
    return if (lastIndex != INVALID)
        date.substring(START, lastIndex + TIME_DIGIT)
    else date
}

fun String.maskEmail(): String {
    return this.replace("(?<=.).(?=[^@]*?@)".toRegex(), "*")
}

fun String.maskName(): String {
    var name = ""
    var firstName = ""
    this.split("\\s".toRegex()).forEachIndexed { index, s ->
        if (index == 0) {
            firstName = s.substring(0, 1) + "*".repeat(s.length - 1)

        } else
            name = "$name $s"
    }
    return firstName + name
}

fun String.maskPhone(): String {
    return this.replaceRange(2, this.length - 4, "*".repeat(this.length - 6))
}

fun String?.formatterRootCNPJ(): String {
    return if (this?.isNotEmpty() == true) this.substring(0, 8).let {
        FormHelper.maskFormatter(this, CNPJ_MASK_INITIAL).formattedText.string
    } else ""
}

fun String?.clearCNPJMask(): String {
    return if (this?.isNotEmpty() == true) this.replace("[^0-9]".toRegex(), "")
    else ""
}

fun CharSequence?.onlyDigits(): String {
    return this?.replace("[\\D]".toRegex(), "") ?: ""
}

fun String.maskCPF(): String {
    val start = this.replaceRange(0, 3, "*".repeat(3))
    return start.maskCPFEnd()
}

fun String.maskCPFEnd(): String {
    return this.replaceRange(12, this.length, "*".repeat(2))
}

fun String.fromHtml(): CharSequence {

    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_COMPACT
    )
}

fun String.documentWithoutMask() = this.trim().replace(DOT, EMPTY).replace(SIMPLE_LINE, EMPTY)

fun String.isNotBooting() = this == ERROR_NOT_BOOTING

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun Double?.formatRate() = this?.let { rate ->
    RATE_FORMAT.format(Locale(PT, BR), rate)
} ?: SIMPLE_LINE

fun Any?.toStringAndReplaceDotWithComma(): String {
    return this.toString().replace(DOT, COMMA)
}

fun String?.ifNullSimpleLine() =
    this?.let { this.ifEmpty { SIMPLE_LINE } } ?: SIMPLE_LINE

/**
 * Extension function for nullable String objects.
 *
 * @receiver String? The nullable String object this function is called on.
 * @param block A lambda function that returns a String. This function is executed if the receiver is null or blank.
 * @return If the receiver is null or blank, the result of the block function is returned. Otherwise, the receiver itself is returned.
 */
fun String?.ifNullOrBlank(block: () -> String) = if (isNullOrBlank()) block() else this

fun String.formatStringToPhone(): String {
    var phoneNumber = this.removeNonNumbers()
    phoneNumber = if (phoneNumber.length > ELEVEN) takeLast(ELEVEN) else phoneNumber
    val phoneMask = if (phoneNumber.length > TEN) Mask(PHONE_MASK_FORMAT) else Mask(LANDLINE_MASK_FORMAT)

    return phoneMask.apply(CustomCaretString.forward(phoneNumber)).formattedText.string
}

fun String?.ifNullOrBlank(value: String): String {
    return if (this.isNullOrBlank()) {
        value
    } else {
        this
    }
}

fun addMaskCPForCNPJ(textoAFormatar: String?, mask: String): String {
    var formatado = ""
    var i = 0
    // vamos iterar a mascara, para descobrir quais caracteres vamos adicionar e quando...
    for (m in mask.toCharArray()) {
        if (m != '#') { // se não for um #, vamos colocar o caracter informado na máscara
            formatado += m
            continue
        }
        // Senão colocamos o valor que será formatado
        try {
            formatado += textoAFormatar?.get(i)
        } catch (e: Exception) {
            break
        }
        i++
    }
    return formatado
}
