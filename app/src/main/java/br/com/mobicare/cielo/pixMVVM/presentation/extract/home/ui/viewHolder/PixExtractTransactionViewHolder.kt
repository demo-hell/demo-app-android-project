package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK_DAY_MONTH
import br.com.mobicare.cielo.commons.utils.isToday
import br.com.mobicare.cielo.commons.utils.orZero
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toStringDayAndMonthOrTimeOfDay
import br.com.mobicare.cielo.databinding.ItemPixExtractTransactionBinding
import br.com.mobicare.cielo.extensions.applyStrikeThru
import br.com.mobicare.cielo.extensions.removeStrikeThru
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixExtractReceiptType
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract.PixExtractReceipt
import br.com.mobicare.cielo.pixMVVM.domain.model.PixReceiptsScheduled
import java.time.ZonedDateTime

class PixExtractTransactionViewHolder(
    private val binding: ItemPixExtractTransactionBinding,
) : RecyclerView.ViewHolder(binding.root) {
    private val context = binding.root.context

    fun bind(
        receipt: Any,
        onItemClick: (Any) -> Unit,
    ) {
        when (receipt) {
            is PixExtractReceipt -> {
                binding.apply {
                    root.setCustomDrawable {
                        radius = R.dimen.dimen_12dp
                        solidColor = R.color.white
                    }
                    tvTitle.text = receipt.type?.title?.let { context.getString(it) }
                    tvTransactionDate.text = receipt.date.toStringDayAndMonthOrTimeOfDay()
                    tvValue.text = getValueTransaction(receipt)
                    tvOperatorName.text = getOperatorName(receipt)
                    root.contentDescription =
                        receipt.type?.let { createContextDescription(it, receipt.date, receipt.finalAmount.orZero()) }.orEmpty()
                    root.setOnClickListener { onItemClick(receipt) }
                }
                receipt.type?.let { setupIcon(receipt.transactionStatus.orEmpty(), it) }
                applyCancelStyle(receipt.transactionStatus.orEmpty())
            }

            is PixReceiptsScheduled.Item.Receipt -> {
                binding.apply {
                    root.setCustomDrawable {
                        radius = R.dimen.dimen_12dp
                        solidColor = R.color.white
                    }
                    tvTitle.text = receipt.type?.title?.let { context.getString(it) }
                    tvTransactionDate.text = receipt.schedulingDate?.parseToString(SIMPLE_DT_FORMAT_MASK_DAY_MONTH).orEmpty()
                    tvValue.text = receipt.finalAmount?.toPtBrRealString().orEmpty()
                    tvOperatorName.text = receipt.payeeName
                    root.contentDescription =
                        receipt.type?.let { createContextDescription(it, null, receipt.finalAmount.orZero()) }.orEmpty()
                    root.setOnClickListener { onItemClick(receipt) }
                }
                receipt.type?.let { setupIcon(receipt.transactionStatus, it) }
                applyCancelStyle(receipt.transactionStatus)
            }
        }
    }

    private fun getValueTransaction(receipt: PixExtractReceipt): String =
        if (receipt.type?.isReceipt == true) {
            receipt.finalAmount?.toPtBrRealString().orEmpty()
        } else {
            context.getString(
                R.string.pix_extract_amount_negative_mask,
                receipt.finalAmount?.toPtBrRealString().orEmpty(),
            )
        }

    private fun setupIcon(
        transactionStatus: String,
        type: PixExtractReceiptType,
    ) {
        when (transactionStatus) {
            PixTransactionStatusEnum.CANCELLED.name,
            PixTransactionStatusEnum.FAILED.name,
            PixTransactionStatusEnum.NOT_EXECUTED.name,
            PixTransactionStatusEnum.SENT_WITH_ERROR.name,
            -> {
                applyBackgroundIcon(R.color.surface_neutral)
                setIcon(R.drawable.ic_symbols_slash_neutral_main_20_dp, R.color.neutral_main)
            }

            PixTransactionStatusEnum.PENDING.name, PixTransactionStatusEnum.PROCESSING.name -> {
                applyBackgroundIcon(R.color.surface_warning)
                setIcon(
                    R.drawable.ic_date_time_clock_neutral_soft_20_dp,
                    R.color.feedback_warning_dark,
                )
            }

            else -> {
                applyBackgroundIcon(type.backgroundIconColor)
                setIcon(type.icon, type.iconColor)
            }
        }
    }

    private fun getOperatorName(receipt: PixExtractReceipt): String =
        when {
            receipt.type == PixExtractReceiptType.SCHEDULE_DEBIT || receipt.type?.isReceipt == true -> receipt.payeeName.orEmpty()
            receipt.type == PixExtractReceiptType.FEE_DEBIT -> context.getString(R.string.pix_extract_operator_name_card_fee_debit)
            else -> receipt.payerName.orEmpty()
        }

    private fun createContextDescription(
        type: PixExtractReceiptType,
        date: ZonedDateTime?,
        amount: Double,
    ): String =
        context.getString(
            when {
                type == PixExtractReceiptType.SCHEDULE_DEBIT || type == PixExtractReceiptType.SCHEDULE_RECURRENCE_DEBIT -> {
                    R.string.pix_extract_content_description_transaction_item_scheduling
                }

                date?.isToday() == true -> R.string.pix_extract_content_description_transaction_item_with_hour
                else -> R.string.pix_extract_content_description_transaction_item_with_date
            },
            binding.tvTitle.text,
            amount.toPtBrRealString(false),
            binding.tvOperatorName.text,
            binding.tvTransactionDate.text,
        )

    private fun applyBackgroundIcon(
        @ColorRes color: Int,
    ) {
        binding.llContainerIcon.setCustomDrawable {
            radius = R.dimen.dimen_4dp
            solidColor = color
        }
    }

    private fun setIcon(
        @DrawableRes icon: Int,
        @ColorRes color: Int,
    ) {
        binding.ivIcon.apply {
            setImageResource(icon)
            setColorFilter(ContextCompat.getColor(context, color))
        }
    }

    private fun applyCancelStyle(transactionStatus: String) {
        when (transactionStatus) {
            PixTransactionStatusEnum.CANCELLED.name,
            PixTransactionStatusEnum.FAILED.name,
            PixTransactionStatusEnum.NOT_EXECUTED.name,
            PixTransactionStatusEnum.SENT_WITH_ERROR.name,
            -> {
                binding.apply {
                    tvTitle.applyStrikeThru()
                    tvOperatorName.applyStrikeThru()
                    tvValue.applyStrikeThru()
                }
            }

            else -> {
                binding.apply {
                    tvTitle.removeStrikeThru()
                    tvOperatorName.removeStrikeThru()
                    tvValue.removeStrikeThru()
                }
            }
        }
    }
}
