package br.com.mobicare.cielo.commons.utils.spannable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import androidx.annotation.ColorInt
import br.com.mobicare.cielo.commons.constants.EIGHT
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.utils.dp

class CustomBulletSpan(
        private val bulletRadius: Int = STANDARD_BULLET_RADIUS,
        private val gapWidth: Int = STANDARD_GAP_WIDTH,
        @ColorInt private val circleColor: Int = STANDARD_COLOR
) : LeadingMarginSpan {

    private companion object {
        val STANDARD_BULLET_RADIUS = TWO.dp()
        val STANDARD_GAP_WIDTH = EIGHT.dp()
        const val STANDARD_COLOR = Color.BLACK
    }

    private val circlePaint = Paint().apply {
    color = circleColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return TWO * bulletRadius + gapWidth
    }

    override fun drawLeadingMargin(
            canvas: Canvas, paint: Paint, x: Int, dir: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            first: Boolean,
            layout: Layout?
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val yPosition = (top + bottom) / TWO.toFloat()
            val xPosition = (x + dir * bulletRadius).toFloat()

            canvas.drawCircle(xPosition, yPosition, bulletRadius.toFloat(), circlePaint)
        }
    }
}