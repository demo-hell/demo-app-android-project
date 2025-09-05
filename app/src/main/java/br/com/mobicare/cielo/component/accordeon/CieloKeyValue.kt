package br.com.mobicare.cielo.component.accordeon

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.databinding.LayoutKeyValueBinding
import br.com.mobicare.cielo.extensions.visible

class CieloKeyValue @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var binding: LayoutKeyValueBinding? = null

    init {
        binding = LayoutKeyValueBinding.inflate(
                LayoutInflater.from(context),
                this,
                true
        )
    }

    fun setData(key: String, value: String, isShowDivider: Boolean = true, @ColorRes colorText: Int? = null) {
        binding?.apply {
            tvKey.text = key
            tvValue.text = value
            colorText?.let {
                tvKey.setTextColor(ContextCompat.getColor(context, it))
                tvValue.setTextColor(ContextCompat.getColor(context, it))
            }
            dividerView.visible(isShowDivider)
        }
    }

}