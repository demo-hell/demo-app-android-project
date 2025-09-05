package br.com.mobicare.cielo.commons.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.pix.constants.EMPTY

object AccessibilityUtils {

    fun convertAmount(amount: Double, context: Context): String {
        return try {
            val isNegative = amount < ZERO_DOUBLE
            val amountString = amount.toPtBrRealStringWithoutSymbol().replace(Text.SIMPLE_LINE, EMPTY).replace(DOT, EMPTY).split(COMMA)
            val amountInteger = amountString[ZERO].toInt()
            val amountCents = amountString[ONE].toInt()

            val text = if (amountInteger > ONE && amountCents > ONE) {
                context.getString(R.string.content_description_amount_reais_and_centavos, amountInteger, amountCents)
            } else if (amountInteger > ONE && amountCents == ONE) {
                context.getString(R.string.content_description_amount_reais_and_centavo, amountInteger)
            } else if (amountInteger > ONE && amountCents == ZERO) {
                context.getString(R.string.content_description_amount_reais, amountInteger)
            } else if (amountInteger == ONE && amountCents > ONE) {
                context.getString(R.string.content_description_amount_real_and_centavos, amountCents)
            } else if (amountInteger == ONE && amountCents == ONE) {
                context.getString(R.string.content_description_amount_real_and_centavo)
            } else if (amountInteger == ONE && amountCents == ZERO) {
                context.getString(R.string.content_description_amount_real)
            } else if (amountInteger == ZERO && amountCents > ONE) {
                context.getString(R.string.content_description_amount_centavos, amountCents)
            } else if (amountInteger == ZERO && amountCents == ONE) {
                context.getString(R.string.content_description_amount_centavo)
            } else {
                context.getString(R.string.content_description_amount_zero_real)
            }

            if (isNegative) context.getString(R.string.content_description_amount_negative) + text else text
        } catch (e: Exception) {
            EMPTY
        }
    }

    fun descriptionForSimpleListHorizontal(context: Context, itemsChild: List<String?>, position: Int, length: Int): String {
        val descriptionItems = itemsChild.reduce { acc, it -> acc + Text.DOT_WITH_SPACE + it }
        return if ((position + ONE) == length)
            context.getString(R.string.content_description_list_simple_horizontal_end, (position + ONE), length, descriptionItems)
        else
            context.getString(R.string.content_description_list_simple_horizontal, (position + ONE), length, descriptionItems)
    }

}