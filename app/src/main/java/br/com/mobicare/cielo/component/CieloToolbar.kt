package br.com.mobicare.cielo.component

import android.content.Context
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.activity_main_bottom_navigation.view.*

class CieloToolbar : Toolbar {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var isHome: Boolean = false

    fun configure(isHome: Boolean, title: String, colorResId: Int) {
        this.isHome = isHome

        textToolbarMainTitle.text = SpannableStringBuilder.valueOf(title)
        configureColorResId(colorResId)
        configureVisibility()
        configureAccessibility()
    }

    private fun configureVisibility() {
        if (this.isHome) {
            this.visibility = GONE
        } else {
            this.visibility = VISIBLE
        }
    }

    private fun configureAccessibility() {
        if(!this.isHome) textToolbarMainTitle.contentDescription = context.getString(R.string.description_title_toolbar, title)
    }

    private fun configureColorResId(colorResId: Int) {
        if (colorResId != -1)
            toolbarHome.setBackgroundColor(ContextCompat
                .getColor(context, colorResId))
        else
            toolbarHome.setBackgroundColor(ContextCompat
                .getColor(context, R.color.color_017CEB))

    }
}
