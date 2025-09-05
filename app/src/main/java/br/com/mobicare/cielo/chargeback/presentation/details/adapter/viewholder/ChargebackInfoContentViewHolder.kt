package br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder

import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.helper.widget.Layer
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContent
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContentField
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContentFieldType
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.databinding.ItemChargebackInfoBinding
import br.com.mobicare.cielo.extensions.*

class ChargebackInfoContentViewHolder(
    private val binding: ItemChargebackInfoBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context
    private var onItemReasonClicked: (() -> Unit)? = null
    private var onItemMessageClicked: ((String) -> Unit)? = null

    fun bind(item: ChargebackInfoContent) {
        binding.apply {
            buildField(item.firstField, tvLabel1, ivIcon1, tvContent1, layerField1)
            buildField(item.secondField, tvLabel2, ivIcon2, tvContent2, layerField2)

            if(item.hideSecondField)
                layerField2.invisible()

        }
    }

    fun setOnItemReasonClicked(callback: () -> Unit) {
        onItemReasonClicked = callback
    }

    fun setOnItemMessageClicked(callback: (String) -> Unit) {
        onItemMessageClicked = callback
    }

    private fun buildField(
        field: ChargebackInfoContentField?,
        labelView: TextView,
        iconView: ImageView,
        contentView: TextView,
        layerView: Layer
    ) {
        setupLabelText(field, labelView)

        if (field?.contentText.isNullOrEmpty()) {
            if (field?.type != ChargebackInfoContentFieldType.MESSAGE)
                contentView.text = EMPTY
            else
                layerView.invisible()

            return
        }

        setupContainerField(layerView)
        setupContentIcon(field, iconView)
        setupContentText(field, contentView)

        when (field?.type) {
            ChargebackInfoContentFieldType.REASON ->
                customizeReasonField(field, contentView, layerView)
            ChargebackInfoContentFieldType.MESSAGE ->
                customizeMessageField(field, contentView, iconView, layerView)
            ChargebackInfoContentFieldType.CARD_BRAND ->
                customizeCardBrandField(iconView)
            ChargebackInfoContentFieldType.DEFAULT ->
                customizeDefaultField(contentView, iconView)
        }
    }

    private fun setupLabelText(field: ChargebackInfoContentField?, labelView: TextView) {
        labelView.text = field?.labelText
    }

    private fun setupContentIcon(field: ChargebackInfoContentField?, iconView: ImageView) {
        if (field?.contentIcon != null)
            iconView.apply {
                setImageResource(field.contentIcon)
                contentDescription = field.labelText
                visible()
            }
        else
            iconView.gone()
    }

    private fun setupContentText(field: ChargebackInfoContentField?, contentView: TextView) {
        contentView.text = field?.contentText
    }

    private fun setupContainerField(layerView: Layer) {
        layerView.apply {
            setOnClickListener(null)
            setBackgroundResource(ZERO)
            visible()
        }
    }

    private fun customizeDefaultField(contentView: TextView, iconView: ImageView) {
        contentView.setTextColor(getColor(R.color.color_5A646E))

        iconView.apply {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            requestLayout()
        }
    }

    private fun customizeReasonField(
        field: ChargebackInfoContentField,
        contentView: TextView,
        layerView: Layer
    ) {
        contentView.setTextColor(getColor(R.color.brand_400))
        layerView.apply {
            setOnClickListener {
                onItemReasonClicked?.invoke()
            }
            setBackgroundResource(R.drawable.background_ripple_white)
        }
    }

    private fun customizeCardBrandField(iconView: ImageView) {
        iconView.apply {
            layoutParams.width = context.resources.getDimensionPixelSize(R.dimen.dimen_32dp)
            scaleType = ImageView.ScaleType.FIT_START
            requestLayout()
        }
    }

    private fun customizeMessageField(
        field: ChargebackInfoContentField,
        contentView: TextView,
        iconView: ImageView,
        layerView: Layer
    ) {
        iconView.setColorFilter(getColor(R.color.color_5A646E), PorterDuff.Mode.SRC_IN)
        isTextTruncated(contentView) {
            iconView.setColorFilter(getColor(R.color.brand_400), PorterDuff.Mode.SRC_IN)
            contentView.setTextColor(getColor(R.color.brand_400))
            layerView.apply {
                setOnClickListener {
                    onItemMessageClicked?.invoke(field.contentText ?: EMPTY)
                }
                setBackgroundResource(R.drawable.background_ripple_white)
            }
        }
    }

    private fun getColor(@ColorRes colorRes: Int) = ContextCompat.getColor(context, colorRes)

    private fun isTextTruncated(textView: TextView, callback: () -> Unit) {
        textView.viewTreeObserver.addOnDrawListener {
            val layout = textView.layout
            if (layout != null) {
                val lines = layout.lineCount
                if (lines > ZERO) {
                    val ellipsisCount = layout.getEllipsisCount(lines - ONE)
                    if (ellipsisCount > ZERO) callback()
                }
            }
        }
    }

}