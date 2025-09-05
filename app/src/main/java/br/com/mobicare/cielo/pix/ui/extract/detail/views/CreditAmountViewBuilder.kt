package br.com.mobicare.cielo.pix.ui.extract.detail.views

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailCreditAmountBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.domain.Fee
import br.com.mobicare.cielo.pix.domain.Settlement
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.*
import br.com.mobicare.cielo.pix.ui.extract.detail.views.helpers.CreditAmountDataHelper

class CreditAmountViewBuilder(
    private val layoutInflater: LayoutInflater,
    private val data: TransferDetailsResponse
) {

    private val binding by lazy { LayoutPixExtractDetailCreditAmountBinding.inflate(layoutInflater) }
    private val context get() = binding.root.context

    private val helper = CreditAmountDataHelper(data)

    private var shouldApplyStrikeThrough: Boolean = false
    private var onFeeClick: ((Fee?) -> Unit)? = null
    private var onNetAmountClick: ((Settlement?) -> Unit)? = null

    private val feeText get() = helper.run {
        if (isFeePendingOrProcessing)
            context.getString(R.string.text_pix_extract_detail_to_be_discounted)
        else
            formattedTariffAmount
    }

    private val netAmountText get() = helper.run {
        if (isSettlementProcessing)
            context.getString(R.string.text_pix_extract_detail_processing)
        else if (isSettlementCompletelyExecuted || isSettlementPartiallyExecuted)
            formattedSettlementFinalAmount
        else null
    }

    fun build(): View {
        if (helper.isTransferOperationType && helper.hasFeeOrSettlement)
            buildTransferOperationType()
        else if (helper.isChangeOperationType && helper.hasFeeOrSettlement)
            buildChangeOperationType()
        else return SimpleAmountViewBuilder(layoutInflater, data).run {
            setTransactionType(TransactionTypeEnum.TRANSFER_CREDIT)
            setStrikeThrough(shouldApplyStrikeThrough)
            build()
        }

        return binding.root
    }

    private fun buildTransferOperationType() {
        configureAmount()
        configureFeeAmount()
        configureNetAmount()
    }

    private fun buildChangeOperationType() {
        configureAmount()
        configureFeeAmount()
        configureNetAmount()
        configureChangeType()
    }

    fun setStrikeThrough(value: Boolean): CreditAmountViewBuilder {
        shouldApplyStrikeThrough = value
        return this
    }

    fun setOnFeeClickListener(listener: (Fee?) -> Unit): CreditAmountViewBuilder {
        onFeeClick = listener
        return this
    }

    fun setOnNetAmountClickListener(listener: (Settlement?) -> Unit): CreditAmountViewBuilder {
        onNetAmountClick = listener
        return this
    }

    private fun configureAmount() {
        binding.tvTransactionAmount.apply {
            text = data.amount?.toPtBrRealString()
            mayApplyStrikeThrough(this)
        }
    }

    private fun configureFeeAmount() {
        binding.apply {
            if (helper.hasFee.not()) {
                containerFee.gone()
                return
            }
            tvFeeAmount.apply {
                text = feeText
                setFeeClickable(helper.isFeeClickable)
                mayApplyStrikeThrough(this)
            }
        }
    }

    private fun configureNetAmount() {
        binding.apply {
            if (netAmountText.isNullOrEmpty()) {
                dividerNetAmount.gone()
                containerNetAmount.gone()
                return
            }
            tvNetAmount.apply {
                text = netAmountText
                if (helper.isSettlementExecuted) {
                    mayApplyStrikeThrough(this)
                    setNetAmountClickable(helper.isNetAmountClickable)
                }
            }
        }
    }

    private fun configureChangeType() {
        binding.apply {
            data.changeAmount?.let {
                containerQrCode.visible()
                tvChangeValue.text = data.changeAmount.toPtBrRealString()
                tvSaleValue.text = data.purchaseAmount?.toPtBrRealString()
            }
            tvTransactionAmount.setTextAppearance(R.style.regular_ubuntu_24_success_500)
            tvLabelAmountReceived.gone()
            tvLabelValue.visible()
        }
    }

    private fun setFeeClickable(value: Boolean) {
        binding.apply {
            containerFee.apply {
                isClickable = value
                isFocusable = value
                if (value) {
                    contentDescription = context.getString(
                        R.string.text_pix_extract_detail_content_description_button_fee, feeText)
                    setOnClickListener { onFeeClick?.invoke(data.fee) }
                } else {
                    background = null
                    setOnClickListener(null)
                }
            }
            tvLabelFee.apply {
                setTextColor(selectTextColor(value))
                importantForAccessibility = selectImportantForAccessibilityMode(value.not())
            }
            tvFeeAmount.apply {
                setTextColor(selectTextColor(value))
                importantForAccessibility = selectImportantForAccessibilityMode(value.not())
            }
        }
    }

    private fun setNetAmountClickable(value: Boolean) {
        binding.apply {
            containerNetAmount.apply {
                isClickable = value
                isFocusable = value
                if (value) {
                    contentDescription = context.getString(
                        R.string.text_pix_extract_detail_content_description_button_net_amount, netAmountText)
                    setOnClickListener { onNetAmountClick?.invoke(data.settlement) }
                } else {
                    background = null
                    setOnClickListener(null)
                }
            }
            tvLabelNetAmount.apply {
                setTextColor(selectTextColor(value))
                importantForAccessibility = selectImportantForAccessibilityMode(value.not())
            }
            tvNetAmount.apply {
                setTextColor(selectTextColor(value))
                importantForAccessibility = selectImportantForAccessibilityMode(value.not())
            }
        }
    }

    private fun selectImportantForAccessibilityMode(isImportant: Boolean) =
        if (isImportant) View.IMPORTANT_FOR_ACCESSIBILITY_YES else View.IMPORTANT_FOR_ACCESSIBILITY_NO

    private fun selectTextColor(isClickable: Boolean) =
        ContextCompat.getColor(context, if (isClickable) R.color.brand_400 else R.color.success_500)

    private fun mayApplyStrikeThrough(textView: TextView) {
        if (shouldApplyStrikeThrough)
            textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }

}