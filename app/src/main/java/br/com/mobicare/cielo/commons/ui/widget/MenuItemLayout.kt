package br.com.mobicare.cielo.commons.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import br.com.mobicare.cielo.R


class MenuItemLayout @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {


    init {
        LayoutInflater.from(context)
                .inflate(R.layout.layout_menu_item, this, true)

    }

}