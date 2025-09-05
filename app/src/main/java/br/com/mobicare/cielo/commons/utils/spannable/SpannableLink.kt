package br.com.mobicare.cielo.commons.utils.spannable

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View


class SpannableLink(private val clickFunc: () -> Unit, private val isUnderline: Boolean = true) :
    ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderline
    }

    override fun onClick(p0: View) {
        clickFunc()
    }
}