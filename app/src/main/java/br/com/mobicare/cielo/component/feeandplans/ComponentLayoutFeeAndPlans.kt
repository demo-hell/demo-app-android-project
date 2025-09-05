package br.com.mobicare.cielo.component.feeandplans

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.feeandplans.model.ComponentLayoutFeeAndPlansItem
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.item_content_fee_and_plans.view.*
import kotlinx.android.synthetic.main.layout_component_fee_and_plan.view.*
import org.jetbrains.annotations.NotNull

class ComponentLayoutFeeAndPlans @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private var textViewFooter: AppCompatTextView
    private var textViewTitle: AppCompatTextView

    var showFooter: Boolean = false
        set(value) {
            hideFooter(value)
            field = value
            invalidate()
            requestLayout()
        }

    var showTitle: Boolean = false
        set(value) {
            field = value
            if (field) textViewTitle.visible() else textViewTitle.gone()
            invalidate()
            requestLayout()
        }

    init {
        View.inflate(context, R.layout.layout_component_fee_and_plan, this)
        textViewFooter = findViewById(R.id.textViewFooter)
        textViewTitle = findViewById(R.id.textViewTitle)

        val attribute = context.obtainStyledAttributes(attrs, R.styleable.ComponentLayoutFeeAndPlans)
        showTitle = attribute.getBoolean(R.styleable.ComponentLayoutFeeAndPlans_showTitle, true)
        showFooter = attribute.getBoolean(R.styleable.ComponentLayoutFeeAndPlans_showFooter, false)
        attribute.recycle()
    }

    fun setContent(title: String = "",
                   @NotNull contentList: ArrayList<ComponentLayoutFeeAndPlansItem>) {
        textViewTitle.text = title
        contentList.forEachIndexed { index, feeAndPlansItem ->
            val isLast = index == contentList.size - 1

            val item = inflate(context, R.layout.item_content_fee_and_plans, null)
            item.textViewLabel.text = feeAndPlansItem.labelTitle
            item.textViewValue.text = feeAndPlansItem.labelValue
            item.textViewValue.setTextColor(ContextCompat.getColor(context, feeAndPlansItem.labelValueColor))
            feeAndPlansItem.labelSubTitle?.let {
                item.textViewLabelSubtitle.text = it
                item.textViewLabelSubtitle.visible()
            }
            if (!showFooter && isLast) item.viewLineSeparator.gone()

            linearLayoutContent.addView(item)
        }
    }

    fun clearContent() {
        linearLayoutContent.removeAllViews()
    }

    private fun hideFooter(isShowFooter: Boolean) {
        if (isShowFooter) textViewFooter.visible()
        else textViewFooter.gone()
    }
}