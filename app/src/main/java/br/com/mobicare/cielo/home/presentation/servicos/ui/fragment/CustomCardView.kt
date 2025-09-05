package br.com.mobicare.cielo.home.presentation.servicos.ui.fragment

import android.content.Context
import android.util.AttributeSet

/**
 * Created by david on 11/08/17.
 */

class CustomCardView : androidx.cardview.widget.CardView {

    constructor(ctx: Context) : super(ctx, null)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}

