package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.utils.DATE_FORMAT_PIX_TRANSACTION
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailReceiptBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailReceiptDividerBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailReceiptItemBinding
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailReceiptTitleBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

abstract class PixReceiptBaseViewBuilder(
    private val layoutInflater: LayoutInflater,
) {
    protected val binding = LayoutPixExtractDetailReceiptBinding.inflate(layoutInflater)
    protected val context: Context = binding.root.context

    open fun build(): View {
        binding.ivShare.setCustomDrawable {
            strokeWidth = FOUR
            strokeColor = R.color.brand_400
            shape = GradientDrawable.OVAL
        }

        return binding.root
    }

    protected fun addView(view: View) {
        binding.containerContent.addView(view)
    }

    protected fun buildItemView(
        labelText: String,
        valueText: String?,
        captionText: String?,
        onFieldTap: (() -> Unit)? = null,
        @DimenRes paddingTop: Int? = null,
    ): View =
        LayoutPixExtractDetailReceiptItemBinding.inflate(layoutInflater).run {
            tvLabel.text = labelText
            tvValue.text = valueText
            captionText?.let {
                tvCaption.apply {
                    text = it
                    visible()
                }
            }
            onFieldTap?.let {
                tvValue.apply {
                    setTextColor(
                        ContextCompat.getColor(context, R.color.brand_400),
                    )
                    setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        ContextCompat.getDrawable(context, R.drawable.ic_directions_chevron_right_brand_400_16_dp),
                        null,
                    )
                }
                containerItem.apply {
                    isClickable = true
                    setOnClickListener { it() }
                }
            }
            paddingTop?.let {
                root.updatePadding(top = context.resources.getDimensionPixelOffset(it))
            }
            root
        }

    protected fun buildTitleView(titleText: String): View =
        LayoutPixExtractDetailReceiptTitleBinding.inflate(layoutInflater).run {
            tvTitle.text = titleText
            root
        }

    protected fun buildDividerView(): View = LayoutPixExtractDetailReceiptDividerBinding.inflate(layoutInflater).root

    protected fun getString(
        @StringRes resId: Int,
    ) = context.getString(resId)

    protected fun getAboutSettlement(settlement: PixTransferDetail.Settlement): String? {
        return when (settlement.settlementTransactionStatus) {
            PixTransactionStatus.SCHEDULED -> {
                getString(R.string.pix_extract_detail_about_settlement_scheduled)
            }

            PixTransactionStatus.EXECUTED -> {
                settlement.settlementDate?.let { date ->
                    context.getString(
                        R.string.pix_extract_detail_about_settlement_executed,
                        date.parseToString(DATE_FORMAT_PIX_TRANSACTION),
                    )
                }
            }

            else -> null
        }
    }
}
