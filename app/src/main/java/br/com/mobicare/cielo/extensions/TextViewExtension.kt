package br.com.mobicare.cielo.extensions

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat

fun TextView.fromHtml(@StringRes idRes: Int, vararg formatArgs: Any) {
    val text = this.context.getString(idRes, *formatArgs)
    val spannableString = SpannableString(
        HtmlCompat
            .fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    )

    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.setColouredSpan(substring: String, color: Int) {
    val spannableString = SpannableString(text)
    val start = text.indexOf(substring)
    val end = start + substring.length
    try {
        spannableString.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spannableString
    } catch (e: IndexOutOfBoundsException) {
        println("'$substring' was not not found in TextView text")
    }
}