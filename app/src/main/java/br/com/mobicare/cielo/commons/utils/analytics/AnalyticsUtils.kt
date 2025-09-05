package br.com.mobicare.cielo.commons.utils.analytics

import android.os.Bundle
import br.com.mobicare.cielo.commons.analytics.ANALYTICS_TAG
import br.com.mobicare.cielo.commons.analytics.GA4_TAG
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.USER_PROFILE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate.USER_TYPE
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.SCREEN_VIEW_PREFIX
import br.com.mobicare.cielo.commons.analytics.USER_PROPERTIES
import br.com.mobicare.cielo.commons.utils.getCurrentDateAndTime
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.text.Normalizer
import java.util.regex.Pattern
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values as GA4

fun consoleLogEvent(event: String, params: Bundle, userProperties: HashMap<String?, String?>) {
    try {
        val properties = userProperties.entries
            .joinToString(" \n", "\n$USER_PROPERTIES { \n", "\n}")
            { (key, value) ->
                "$key = $value"
            }

        val parameters = params.let {
            it.keySet().joinToString(" \n", "\n$event { \n", "\n}")
            { key ->
                "$key = ${it[key]}"
            }
        }
        Timber.tag(ANALYTICS_TAG).d("$properties\n $parameters")

    } catch (exception: Exception) {
        exception.message?.let { error ->
            FirebaseCrashlytics.getInstance().log(error)
        }
    }
}

fun consoleLogEvent(event: String, userProperties: HashMap<*, *>, eventProperties: Bundle) {
    with(Timber.tag(GA4_TAG)) {
        d("==== Google Analytics 4 LOG ====")
        d(getCurrentDateAndTime())

        userProperties.logProperties(USER_PROPERTIES)
        eventProperties.logProperties(event)

        d("================================")
    }
}

private fun HashMap<*, *>.logProperties(label: String) {
    with(Timber.tag(GA4_TAG)) {
        d("$label {")

        forEach { (key, value) ->
            if (value != null && value.toString().isNotEmpty()) d("-> $key = $value")
        }

        d("}")
    }
}

private fun Bundle.logProperties(label: String) {
    with(Timber.tag(GA4_TAG)) {
        d("$label {")

        keySet().forEach { key ->
            val value = get(key)
            if (key == GoogleAnalytics4Events.Other.ITEMS) {
                val items = value as Array<*>
                if (items.isNotEmpty()) {
                    d("-> items = [")
                    items.forEach { item ->
                        if (item != null && item.toString().isNotEmpty()) {
                            d("   $item")
                        }
                    }
                    d("]")
                }
            } else {
                if (value != null && value.toString().isNotEmpty()) d("-> $key = $value")
            }
        }

        d("}")
    }
}

fun List<String?>?.join(normalize: Boolean = true): String? {
    return this?.map { if (normalize) it?.let { normalize(it) } else it }
        ?.filter { it != "" }
        ?.joinToString(" | ")
}

private val listExceptionsUppercaseWords = arrayListOf("CPF", "CNPJ", "EC")

/**
 * Function to normalize expressions in tagging, following the established pattern.
 *
 * This function performs the following normalization steps:
 * 1. Uses only lowercase letters, except for CPF, CNPJ, EC.
 * 2. Inserts spaces between pipes ('|').
 * 3. Does not use hyphens or underscores.
 * 4. Removes accents from characters.
 * 5. Removes any special characters.
 *
 * @param originalText The input string to be normalized.
 * @return The normalized string after applying the specified transformations.
 */
fun normalize(originalText: String?): String {
    return try {
        originalText?.let { value ->
            if (listExceptionsUppercaseWords.contains(value.uppercase()))
                value.uppercase()
            else
                Normalizer.normalize(value, Normalizer.Form.NFD)
                    .replace("[-_/]".toRegex(), " ")
                    .replace("\\p{Mn}+".toRegex(), "")
                    .replace("[^A-Za-z0-9 ]".toRegex(), "")
                    .replace("\\s+".toRegex(), " ")
                    .trim()
                    .lowercase()
        } ?: EMPTY
    } catch (ex: Exception) {
        EMPTY
    }
}

fun pipeJoin(vararg args: String): String {
    return listOf(*args).join().orEmpty()
}

fun createUserPropertiesBundle(
    propertiesMap: MutableMap<String?, String?>,
    eventProperties: MutableList<String>,
    isLoginOrImpersonateFlow: Boolean = false
): Bundle {
    val params = Bundle()

    if (isLoginOrImpersonateFlow.not()) {
        eventProperties.removeAll(listOf(USER_TYPE, USER_PROFILE))
    }

    propertiesMap
        .filterKeys { it in eventProperties }
        .forEach { (key, value) ->
            params.putString(key, value)
        }

    return params
}

fun String.formatScreenName(): String {
    return if (startsWith(SCREEN_VIEW_PREFIX).not()) "$SCREEN_VIEW_PREFIX$this" else this
}

/**
 * Function to removing punctuation, accentuation (diacritics) and replacing space with underline "_" following guidelines GA4
 *
 * @val normalizer = Normalizer.normalize(text, Normalizer.Form.NFD) is normalizes the string using the uncomposed canonical normalization format (NFD), decomposing accented characters into their base combined equivalents and separate diacritics.
 * @val pattern = Pattern.compile("[\\.\\p{InCOMBINING_DIACRITICAL_MARKS}]+") is regular expression that will match any character that is a literal dot or diacritic.
 * @val textFormated = pattern.matcher(normalizer).replaceAll("") uses the compiled regular expression to replace all diacritics in the normalizes string with an empty string, thus removing the accents.
 * @return performs a global replacement of all whitespace in the textFormated string with the underline "_" character, according to the regular expression \\s.
 *
 * example: input: "autorização e erro de processamento."
 *          output: "autorizacao_e_erro_de_processamento"
 */
fun formatTextForGA4(text: String): String {
    val normalizer = Normalizer.normalize(text, Normalizer.Form.NFD)
    val pattern = Pattern.compile("[\\.\\p{InCOMBINING_DIACRITICAL_MARKS}]+")
    val textFormated = pattern.matcher(normalizer).replaceAll("")

    return textFormated.replace(Regex("\\s"), "_")
}