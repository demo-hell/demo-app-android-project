package br.com.mobicare.cielo.commons.secure.presentation.ui.component

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.layout_otp_code_view.view.*
import kotlin.math.absoluteValue

class OTPCodeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var isShowBlue = true
    private var maxValueOriginal: Double = 1.0
    private var maxValue: Int = 100
    private var elapsedTime = (maxValueOriginal / 3.0) * 2

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.layout_otp_code_view, this, true)
    }

    fun setCode(code: String) {
        if (code.isNotEmpty()) {
            if (code.any { it.isDigit() }) {
                var offset = 0
                this.tvDigito01?.text = code.substring(offset++, offset)
                this.tvDigito02?.text = code.substring(offset++, offset)
                this.tvDigito03?.text = code.substring(offset++, offset)
                this.tvDigito04?.text = code.substring(offset++, offset)
                this.tvDigito05?.text = code.substring(offset++, offset)
                this.tvDigito06?.text = code.substring(offset++, offset)
            }
        }
        this.pbCountDownView?.progress = 0
        this.pbCountDownView?.max = 1
    }

    fun setProgress(progress: Double, max: Double) {
        val pbValue = (progress * 100.0).toInt()

        if (this.maxValueOriginal != max) {
            this.maxValue = (max * 100.0).toInt()
            this.maxValueOriginal = max
        }

        this.pbCountDownView?.max = this.maxValue
        this.pbCountDownView?.progress = pbValue

        if (progress >= this.elapsedTime) {
            if (this.isShowBlue) {
                this.isShowBlue = false
                this.pbCountDownView?.progressDrawable = ContextCompat.getDrawable(
                    this.context,
                    R.drawable.progressbar_danger400_drawable
                )
            }
        } else {
            if (!this.isShowBlue) {
                this.isShowBlue = true
                this.pbCountDownView?.progressDrawable = ContextCompat.getDrawable(
                    this.context,
                    R.drawable.progressbar_brand400_drawable
                )
            }
        }
    }

}

fun inversePercentagem(progress: Double, max: Double): Double {
    val diff = (max - progress).absoluteValue
    return diff
}