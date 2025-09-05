package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R


class CircularTimerCountdownView @JvmOverloads constructor(context: Context,
                                                           attrs: AttributeSet? = null,
                                                           defStyleAttr: Int = 0) :
        View(context, attrs, defStyleAttr) {


    private var remainingSectorPaint: Paint? = null
    private val DEFAULT_COLOR = -0xcf9f40
    private var drawingRect: RectF? = null
    private val circleBelow: Paint = Paint()

    private var progressWidth: Int = 0

    /**
     * Countdown phase starting with `1` when a full cycle is remaining and shrinking to `0` the closer the countdown is to zero.
     */
    private var mPhase = 0.0

    init {
        var color = DEFAULT_COLOR
        val theme: Resources.Theme = context.theme
        val appearance: TypedArray = theme.obtainStyledAttributes(
                attrs, R.styleable.CircularTimerCountdownView, 0, 0)
        val n = appearance.indexCount
        for (i in 0 until n) {
            val attr = appearance.getIndex(i)
            if (attr == R.styleable.CircularTimerCountdownView_countdownIndicatorColor) {
                color = appearance.getColor(attr, DEFAULT_COLOR)
            }

            if (attr == R.styleable.CircularTimerCountdownView_progressWidth) {
                progressWidth = appearance.getDimensionPixelSize(attr, 0)
            }

        }
        remainingSectorPaint = Paint().apply {
            this.style = Paint.Style.STROKE
            isAntiAlias = true
            this.strokeWidth = progressWidth.toFloat()
            this.color = ContextCompat.getColor(context, R.color.grey_c5ced6)
        }


        circleBelow.apply {
            this.style = Paint.Style.STROKE
            isAntiAlias = true
            this.strokeWidth = progressWidth.toFloat()
            this.color = color
        }

    }


    override fun onDraw(canvas: Canvas) {
        val remainingSectorSweepAngle = (mPhase * 360).toFloat()
        val remainingSectorStartAngle = 270 - remainingSectorSweepAngle

        if (drawingRect == null) {
            drawingRect = RectF(progressWidth.toFloat(), progressWidth.toFloat(),
                    (width - progressWidth.toFloat()),
                    (height - progressWidth.toFloat()))

        }

        canvas.drawArc(drawingRect!!, 0f, 360f, false,
                circleBelow)
        drawCircle(canvas, remainingSectorStartAngle, remainingSectorSweepAngle,
                remainingSectorPaint)

    }

    private fun drawCircle(canvas: Canvas, start: Float, size: Float, paint: Paint?) {
        canvas.drawArc(drawingRect!!,
                start, size, false,
                paint!!)

    }

    /**
     * Sets the phase of this indicator.
     *
     * @param phase phase `[0, 1]`: `1` when the maximum amount of time is remaining,
     * `0` when no time is remaining.
     */
    fun setPhase(phase: Double) {
        require(!(phase < 0 || phase > 1)) { "phase: $phase" }
        mPhase = phase
        invalidate()
    }


}