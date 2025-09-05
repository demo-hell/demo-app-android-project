package br.com.mobicare.cielo.pix.ui.extract.detail.views

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailDebitAmountBinding
import br.com.mobicare.cielo.extensions.applyStrikeThru
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.updateMargins
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.domain.Credit
import br.com.mobicare.cielo.pix.domain.Settlement
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum
import br.com.mobicare.cielo.pix.enums.PixTransferOriginEnum
import br.com.mobicare.cielo.pix.enums.TransactionTypeEnum

class DebitAmountViewBuilder(
    private val layoutInflater: LayoutInflater,
    private val data: TransferDetailsResponse
) {

    private val binding by lazy { LayoutPixExtractDetailDebitAmountBinding.inflate(layoutInflater) }
    private val context get() = binding.root.context

    private var shouldApplyStrikeThrough: Boolean = false
    private var onReceivingAmountClick: ((Credit?) -> Unit)? = null
    private var onNetAmountClick: ((Settlement?) -> Unit)? = null
    private var onReceivingDetailsClick: ((Credit?) -> Unit)? = null

    private val tariffAmountText get() = data.tariffAmount?.let { (-it).toPtBrWithNegativeRealString() }
    private val creditAmountText get() = data.credit?.creditAmount?.toPtBrRealString()
    private val creditFinalAmountText get() = data.credit?.creditFinalAmount?.toPtBrRealString()
    private val finalAmountText get() = data.finalAmount?.let { (-it).toPtBrWithNegativeRealString() }

    private val isFeeType get() = data.pixType == PixQRCodeOperationTypeEnum.FEE.name

    private val isAutomaticTransferType get() = data.pixType == PixQRCodeOperationTypeEnum.TRANSFER.name
            && data.transferOrigin == PixTransferOriginEnum.SETTLEMENT_V2.name

    fun build(): View {
        if (isFeeType)
            buildFeeTransferType()
        else if (isAutomaticTransferType)
            buildAutomaticTransferType()
        else return SimpleAmountViewBuilder(layoutInflater, data).run {
            setTransactionType(TransactionTypeEnum.TRANSFER_DEBIT)
            setStrikeThrough(shouldApplyStrikeThrough)
            build()
        }

        return binding.root
    }

    private fun buildFeeTransferType() {
        when (data.transactionStatus) {
            PixTransactionStatusEnum.EXECUTED.name -> configureExecutedFeeTypeTransfer()
            PixTransactionStatusEnum.PENDING.name -> configurePendingFeeTypeTransfer()
            PixTransactionStatusEnum.NOT_EXECUTED.name,
            PixTransactionStatusEnum.CANCELLED.name,
            PixTransactionStatusEnum.FAILED.name -> configureFailedFeeTypeTransfer()
        }
    }

    private fun buildAutomaticTransferType() {
        when (data.transactionStatus) {
            PixTransactionStatusEnum.EXECUTED.name,
            PixTransactionStatusEnum.PENDING.name -> configureExecutedOrPendingAutomaticTransfer()
            PixTransactionStatusEnum.NOT_EXECUTED.name,
            PixTransactionStatusEnum.CANCELLED.name,
            PixTransactionStatusEnum.FAILED.name -> configureFailedAutomaticTransfer()
        }
    }

    fun setStrikeThrough(value: Boolean): DebitAmountViewBuilder {
        shouldApplyStrikeThrough = value
        return this
    }

    fun setOnReceivingDetailsClickListener(listener: (Credit?) -> Unit): DebitAmountViewBuilder {
        onReceivingDetailsClick = listener
        return this
    }

    fun setOnReceivingAmountClickListener(listener: (Credit?) -> Unit): DebitAmountViewBuilder {
        onReceivingAmountClick = listener
        return this
    }

    fun setOnNetAmountClickListener(listener: (Settlement?) -> Unit): DebitAmountViewBuilder {
        onNetAmountClick = listener
        return this
    }

    private fun configureExecutedFeeTypeTransfer() {
        binding.apply {
            tvAmount.text = tariffAmountText
            tvReceivingAmount.text = creditAmountText
            tvNetAmount.text = creditFinalAmountText
            containerReceivingAmount.visible()
            containerNetAmount.visible()
            tvFeeInfo.visible()
            makeReceivingAmountClickable()
            makeNetAmountClickable()
        }
    }

    private fun configurePendingFeeTypeTransfer() {
        binding.apply {
            tvAmount.text = tariffAmountText
            divider.updateMargins(top = context.resources.getDimensionPixelOffset(R.dimen.dimen_16dp))
            tvFeeInfo.visible()
        }
    }

    private fun configureFailedFeeTypeTransfer() {
        binding.apply {
            tvAmount.apply {
                text = tariffAmountText
                applyStrikeThru()
            }
            tvLabelAmount.applyStrikeThru()
            divider.gone()
            root.updatePadding(bottom = context.resources.getDimensionPixelOffset(R.dimen.dimen_16dp))
        }
    }

    private fun configureExecutedOrPendingAutomaticTransfer() {
        binding.apply {
            configureAutomaticTransfer()
            divider.updateMargins(top = context.resources.getDimensionPixelOffset(R.dimen.dimen_16dp))
            btnReceivingDetails.setOnClickListener { onReceivingDetailsClick?.invoke(data.credit) }
            containerReceivingDetails.visible()
        }
    }

    private fun configureFailedAutomaticTransfer() {
        binding.apply {
            configureAutomaticTransfer()
            tvLabelAmount.applyStrikeThru()
            tvAmount.applyStrikeThru()
            divider.gone()
            root.updatePadding(bottom = context.resources.getDimensionPixelOffset(R.dimen.dimen_16dp))
        }
    }

    private fun configureAutomaticTransfer() {
        binding.apply {
            tvLabelAmount.text = context.getString(R.string.text_pix_transferred_value)
            tvAmount.text = finalAmountText
        }
    }

    private fun makeReceivingAmountClickable() {
        binding.apply {
            containerReceivingAmount.apply {
                isClickable = true
                isFocusable = true
                setOnClickListener { onReceivingAmountClick?.invoke(data.credit) }
                contentDescription = context.getString(
                    R.string.text_pix_extract_detail_content_description_button_receiving_amount, creditAmountText)
            }
            ContextCompat.getColor(context, R.color.brand_400).apply {
                tvLabelReceivingAmount.setTextColor(this)
                tvReceivingAmount.setTextColor(this)
            }
        }
    }

    private fun makeNetAmountClickable() {
        binding.apply {
            containerNetAmount.apply {
                isClickable = true
                isFocusable = true
                setOnClickListener { onNetAmountClick?.invoke(data.settlement) }
                contentDescription = context.getString(
                    R.string.text_pix_extract_detail_content_description_button_net_amount, creditFinalAmountText)
            }
            ContextCompat.getColor(context, R.color.brand_400).apply {
                tvLabelNetAmount.setTextColor(this)
                tvNetAmount.setTextColor(this)
            }
        }
    }

}