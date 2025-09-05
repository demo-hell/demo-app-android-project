package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import br.com.cielo.libflue.button.CieloRegularBlueButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView

class CallToActionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
)  : FrameLayout(context, attrs, defStyleAttr) {

    var onCallToActionClickListener: OnCallToActionClickListener? = null

    interface OnCallToActionClickListener {
        fun onClick()
    }

    init {

        val inflatedView = LayoutInflater.from(context)
                .inflate(R.layout.view_action_to_call, this,
                        true)

        val typedArr = context
                .obtainStyledAttributes(attrs, R.styleable.CallToActionView,
                        0, 0)

        val configuredActionTitle = typedArr
                .getString(R.styleable.CallToActionView_callToActionTitle)
        val configuredButtonActionLabel = typedArr
                .getString(R.styleable.CallToActionView_buttonCallToActionLabel)

        val callToActionTitle = inflatedView
                .findViewById<TypefaceTextView>(R.id.textCallToActionTitle)

        val buttonCallToAction = inflatedView
                .findViewById<CieloRegularBlueButton>(R.id.buttonCallToAction)

        callToActionTitle?.text = SpannableStringBuilder.valueOf(configuredActionTitle)

        configuredButtonActionLabel?.let { buttonLabel ->
            buttonCallToAction?.setText(buttonLabel)
        }

        buttonCallToAction?.setOnClickListener {
            onCallToActionClickListener?.onClick()
        }

        typedArr.recycle()
    }
}