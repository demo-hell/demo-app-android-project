package br.com.mobicare.cielo.commons.utils.spannable

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import java.util.regex.Pattern

fun String.toSpannableString(): SpannableString {
    return SpannableString(
        HtmlCompat
            .fromHtml(
                this,
                HtmlCompat.FROM_HTML_MODE_LEGACY,
            ),
    )
}

fun String.htmlTextFormat(): Spanned {
    return HtmlCompat.fromHtml(
        this,
        HtmlCompat.FROM_HTML_MODE_LEGACY,
    )
}

fun Int.htmlTextFormatWithValue(
    context: Context,
    value: String,
): Spanned {
    return HtmlCompat.fromHtml(
        context.getString(this, value),
        HtmlCompat.FROM_HTML_MODE_LEGACY,
    )
}

fun String.addLinksToText(
    context: Context,
    links: List<LinkSpan>,
): SpannableString {
    val spannableString = SpannableString(this)

    links.forEach { linkSpan ->
        val matcher = Pattern.compile(linkSpan.text).matcher(spannableString)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            val clickSpan =
                object : ClickableSpan() {
                    override fun onClick(view: View) {
                        linkSpan.onClick?.invoke()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.apply {
                            linkSpan.textColor?.let { color = ContextCompat.getColor(context, it) }
                            linkSpan.typeFace?.let { typeface = ResourcesCompat.getFont(context, it) }
                            isUnderlineText = linkSpan.isUnderlineText
                        }
                    }
                }

            spannableString.setSpan(clickSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    return spannableString
}

data class LinkSpan(
    val text: String,
    val onClick: (() -> Unit)? = null,
    @FontRes val typeFace: Int? = null,
    @ColorRes val textColor: Int? = null,
    val isUnderlineText: Boolean = true,
)
