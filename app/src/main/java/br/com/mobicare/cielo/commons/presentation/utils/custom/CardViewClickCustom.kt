package br.com.mobicare.cielo.commons.presentation.utils.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by benhur.souza on 17/07/2017.
 */

class CardViewClickCustom : androidx.cardview.widget.CardView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    /**
     * Intercept touch event so that inner views cannot receive it.

     * If a ViewGroup contains a RecyclerView and has an OnTouchListener or something like that,
     * touch events will be directly delivered to inner RecyclerView and handled by it. As a result,
     * parent ViewGroup won't receive the touch event any longer.
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }


}
