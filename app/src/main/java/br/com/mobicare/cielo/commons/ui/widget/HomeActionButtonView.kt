package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.custom.TypefaceTextView

class HomeActionButtonView @JvmOverloads constructor(context: Context,
                                                     attrs: AttributeSet? = null,
                                                     defStyleAttr: Int = 0) :
        GridLayout(context, attrs, defStyleAttr) {


    var buttonLabel: String? = null
    @DrawableRes var buttonActionIcon: Int = -1

    init {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val inflatedView = LayoutInflater.from(context).inflate(R.layout.view_button_action_home,
                this, true)

        val typedArr = context.obtainStyledAttributes(attrs,
                R.styleable.HomeActionButtonView, 0, 0)


        buttonLabel = typedArr.getString(R.styleable.HomeActionButtonView_buttonPrepaidActionLabel)
        buttonActionIcon = typedArr.getResourceId(R.styleable.HomeActionButtonView_buttonActionIcon,
                -1)


        val buttonInflatedLabel = inflatedView.findViewById<TypefaceTextView>(R.id
                .buttonActionHomeTextLabel)
        val buttonInflatedActionIcon = inflatedView.findViewById<ImageView>(R.id
                .imageHomeActionButtonIcon)

        buttonInflatedLabel.text = SpannableStringBuilder.valueOf(buttonLabel)
        buttonInflatedActionIcon.setImageResource(buttonActionIcon)

        typedArr.recycle()
    }


}