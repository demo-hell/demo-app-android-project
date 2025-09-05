package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatDelegate
import br.com.mobicare.cielo.R

class HeaderButtonView @JvmOverloads constructor(context: Context,
                                                 attrs: AttributeSet? = null,
                                                 defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {

    var buttonLabel: String? = null

    @DrawableRes var headerButtonIcon: Int = -1

    init {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        val inflatedView = LayoutInflater.from(context)
                .inflate(R.layout.view_header_button, this, true)

        val typedArr = context
                .obtainStyledAttributes(attrs, R.styleable.HeaderButtonView, 0, 0)

        buttonLabel = typedArr.getString(R.styleable.HeaderButtonView_buttonLabel)
        headerButtonIcon = typedArr.getResourceId(R.styleable.HeaderButtonView_headerButtonIcon, -1)


        val headerLabel = inflatedView.findViewById<TextView>(R.id.textHeaderLabel)
        val imageHeaderButton = inflatedView.findViewById<ImageView>(R.id.imageHeaderButton)

        headerLabel.text = buttonLabel
        imageHeaderButton.setImageResource(headerButtonIcon)

        typedArr.recycle()
    }
}