package br.com.mobicare.cielo.pix.ui.extract.detail.views

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailSimpleAmountBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum
import br.com.mobicare.cielo.pix.enums.TransactionTypeEnum

class SimpleAmountViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: TransferDetailsResponse
) {

    private val binding = LayoutPixExtractDetailSimpleAmountBinding.inflate(layoutInflater)
    private val context get() = binding.root.context

    private var transactionType: TransactionTypeEnum? = null
    private var shouldApplyStrikeThrough: Boolean = false

    fun build(): View {
        when (transactionType?.name ?: data.transactionType) {
            TransactionTypeEnum.TRANSFER_CREDIT.name -> configureCredit()
            TransactionTypeEnum.TRANSFER_DEBIT.name -> configureDebit()
        }

        return binding.root
    }

    fun setTransactionType(value: TransactionTypeEnum): SimpleAmountViewBuilder {
        transactionType = value
        return this
    }

    fun setStrikeThrough(value: Boolean): SimpleAmountViewBuilder {
        shouldApplyStrikeThrough = value
        return this
    }

    private fun configureCredit() {
        binding.tvTransactionAmount.apply {
            text = data.finalAmount?.toPtBrRealString()
            mayApplyStrikeThrough(this)
        }
        configureChangeDetails()
    }

    private fun configureDebit() {
        binding.apply {
            tvTransactionAmount.apply {
                text = data.finalAmount?.let { (-it).toPtBrWithNegativeRealString() }
                setTextColor(ContextCompat.getColor(context, R.color.cloud_500))
                mayApplyStrikeThrough(this)
            }
            containerAmount.background =
                ContextCompat.getDrawable(context, R.drawable.background_radius8dp_display_50)
        }
        configureChangeDetails()
    }

    private fun configureChangeDetails() {
        binding.apply {
            data.changeAmount?.let {
                containerQrCode.visible()
                tvChangeValue.text = it.toPtBrRealString()
                tvSaleValue.text = data.purchaseAmount?.toPtBrRealString()
                tvSale.text =
                    if (data.transactionType == PixExtractTypeEnum.TRANSFER_CREDIT.name)
                        context.getString(R.string.tv_value_sale_pix)
                    else
                        context.getString(R.string.tv_purchase_amount)
            }
        }
    }

    private fun mayApplyStrikeThrough(textView: TextView) {
        if (shouldApplyStrikeThrough)
            textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    }

}