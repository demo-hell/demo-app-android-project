package br.com.mobicare.cielo.commons.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView


class StepCheckView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyle: Int = 0) :
        LinearLayout(context, attrs, defStyle) {


    var stepsTitles: MutableList<String>? = null
    var currentStep = 0

    init {

        val a = context.obtainStyledAttributes(
                attrs, R.styleable.StepCheckView, defStyle, 0)

        currentStep = a.getInt(R.styleable.StepCheckView_currentStep, 0)


        //inject and do what you want with your view
        val inflatedView = View.inflate(context, R.layout.view_step_check, this) as LinearLayout

        val textFirstLabel = inflatedView.findViewById<TypefaceTextView>(R.id.textFirstLabel)
        val textSecondLabel = inflatedView.findViewById<TypefaceTextView>(R.id.textSecondLabel)


        stepsTitles = a.getTextArray(R.styleable.StepCheckView_stepsTitles)
                .map { it.toString() }.toMutableList()

        stepsTitles?.run {
            textFirstLabel.text = SpannableStringBuilder.valueOf(this[0])
            textSecondLabel.text = SpannableStringBuilder.valueOf(this[1])
        }

        a.recycle()
        

    }


    fun animateStepProgress() {

        val inflatedView = View.inflate(context, R.layout.view_step_check, this) as LinearLayout

        val imageSecondStep = inflatedView.findViewById<ImageView>(R.id.imageSecondCircleStep)

        val checkIcon = inflatedView.findViewById<ImageView>(R.id.imageCheckStep)

        val viewSecondHorizontalLine = inflatedView.findViewById<View>(R.id.viewSecondHorizontalLine)

        val stepProgressVisible = currentStep == 1

        if (stepProgressVisible) {
            imageSecondStep.setImageDrawable(ContextCompat
                    .getDrawable(context, R.drawable.purple_circle))


            viewSecondHorizontalLine.animate().alpha(1f)
                    .setInterpolator(FastOutSlowInInterpolator())
                    .setDuration(500).setListener(object: AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            viewSecondHorizontalLine.setBackgroundColor(ContextCompat
                                    .getColor(context, R.color.purple))
                        }
                    })
        }

        checkIcon.animate().alpha(1f)
                .setInterpolator(FastOutSlowInInterpolator())
                .setDuration(500).setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                checkIcon.visibility = if (stepProgressVisible) View.VISIBLE else View.INVISIBLE
            }
        })

    }

}
