package br.com.mobicare.cielo.extensions

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan

fun String.setSpanBold(arrayStartPositions: Array<Int>, arrayEndPositions: Array<Int>): SpannableString {
    val spannableString = SpannableString(this)
    arrayStartPositions.forEachIndexed { index, startPosition ->
        spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                startPosition,
                arrayEndPositions[index],
                Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }
    return spannableString
}

fun String.setSpanBold(startPosition: Int, endPosition: Int): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            startPosition,
            endPosition,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)

    return spannableString
}