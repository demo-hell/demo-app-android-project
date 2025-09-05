package br.com.mobicare.cielo.commons.utils

import android.text.SpannableString
import br.com.mobicare.cielo.commons.constants.COMMA
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.spannable.CustomBulletSpan
import br.com.mobicare.cielo.extensions.removeAccents
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import com.redmadrobot.inputmask.helper.Mask
import java.lang.Integer.min
import java.text.Normalizer

fun String.removeWhitespaces(): String = this.split(" ").joinToString("") {
    "${it[0].toTitleCase()}${it.subSequence(1, it.length)}"
}

fun String.removeNonNumbers(): String = this.replace("[^\\d]".toRegex(), "")

fun String.isNumeric(): Boolean {
    if (this.isEmpty()) return false
    val sz = this.length
    for (i in 0 until sz) {
        if (!Character.isDigit(this[i]))
            return false
    }
    return true
}

const val CPF_REGEX = "[0-9][0-9][0-9].[0-9][0-9][0-9].[0-9][0-9][0-9]-[0-9][0-9]"
val CPF_REGEX_PATTERN = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$".toRegex()

const val CPF_MASK_FORMAT = "[000].[000].[000]-[00]"
const val CNPJ_MASK_COMPLETE_FORMAT = "[00].[000].[000]/[0000]-[00]"
const val CNPJ_MASK_FORMAT = "[00].[000].[000]"
const val CNPJ_MASK_INITIAL = "[00].[000].[000]/"

const val ZIP_CODE_MASK_FORMAT = "[00000]-[000]"
const val PHONE_MASK_FORMAT = "([00]) [00000]-[0000]"
const val LANDLINE_MASK_FORMAT = "([00]) [0000]-[0000]"
const val INTERNATIONAL_PHONE_MASK_FORMAT = "+[00] ([00]) [9…]"
const val STATE_MASK_FORMAT = "[AA]"
const val NUMBER_MASK_FORMAT = "[0…]"

const val CPF_LENGTH = 14
const val CPF_WITHOUT_MASK_LENGTH = 11
const val PHONE_WITHOUT_MASK_LENGTH = 11

fun String.containsOnlyNumbers(): Boolean = this.matches(Regex("[0-9]+"))

fun String.containsNumbers(): Boolean = this.contains(Regex("[0-9]"))

fun String.containsNumbersAndNotAt(): Boolean = this.contains("[\\d]".toRegex()) &&
        !this.contains("@")

fun String.addMaskCPForCNPJ(mask: String): String {
    var formatado = ""
    var i = 0
    for (m in mask.toCharArray()) {
        if (m != '#') {
            formatado += m
            continue
        }
        try {
            formatado += this[i]
        } catch (e: Exception) {
            break
        }

        i++
    }
    return formatado
}

val String.wrapInDoubleQuotes: String get() = "\"$this\""

fun CharSequence?.toLowerNoAccents(): String {
    return this.removeAccents().toLowerCasePTBR()
}

fun CharSequence?.toLetterWithUnderScore(): String {
    return this?.replace("[^\\w ]".toRegex(), "")
        .toLowerNoAccents().split(" ").joinToString("_")
}

fun List<String>.toBulletedList(): CharSequence {
    return SpannableString(this.joinToString(System.lineSeparator())).apply {
        this@toBulletedList.foldIndexed(ZERO) { index, acc, span ->
            val end = acc + span.length + if (index != this@toBulletedList.size - ONE) ONE else ZERO
            this.setSpan(CustomBulletSpan(), acc, end, ZERO)
            end
        }
    }
}

/**
 * Function to normalize and apply lower snake case pattern.
 *
 * This function performs the following normalization steps:
 * 1. Remove any special characters and accents except for underscore, whitespace and hyphen.
 * 2. Trim the string removing leading and trailing whitespaces.
 * 3. Replace whitespaces and hyphens by underscores.
 * 4. Convert the whole string to lowercase.
 * 5. Remove excess characters
 *
 * @return The normalized string after applying the specified transformations.
 */
fun String.normalizeToLowerSnakeCase() =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("[^A-Za-z0-9_ -]".toRegex(), "")
        .trim()
        .replace("[\\s-]".toRegex(), "_")
        .lowercase()
        .run { substring(ZERO, min(ONE_HUNDRED, this.length)) }

fun String.getFirstTenCharacters(): String {
    return if (length <= TEN) {
        this
    } else {
        substring(ZERO, TEN)
    }
}

fun String.mask(pattern: String) =
    Mask(pattern)
        .apply(CustomCaretString.forward(this))
        .formattedText
        .string

fun String.unmask(pattern: String) =
    Mask(pattern)
        .apply(CustomCaretString.forward(this))
        .extractedValue

fun String?.orSimpleLine() = if (isNullOrBlank()) SIMPLE_LINE else this

fun List<String>.toStringCommaSeparatedAndLast() = when(this.size) {
    ZERO -> EMPTY_VALUE
    ONE -> this.first()
    else -> "${this.dropLast(ONE).joinToString(COMMA + ONE_SPACE)} e ${this.last()}"
}