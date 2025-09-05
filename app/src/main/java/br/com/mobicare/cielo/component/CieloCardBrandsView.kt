package br.com.mobicare.cielo.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import br.com.mobicare.cielo.R
import kotlinx.android.synthetic.main.layout_cielo_card_brands_view.view.*

class CieloCardBrandsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var listener: OnButtonClickListener? = null

    init {
        LayoutInflater
            .from(context)
            .inflate(R.layout.layout_cielo_card_brands_view, this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        this.showAllBrandsButton?.setOnClickListener {
            this.listener?.onButtonClicked()
        }
    }

    fun setListener(listener: OnButtonClickListener) {
        this.listener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClicked()
    }
}