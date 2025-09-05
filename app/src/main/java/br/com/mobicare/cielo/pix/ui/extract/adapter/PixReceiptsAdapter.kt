package br.com.mobicare.cielo.pix.ui.extract.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.LONG_TIME_NO_UTC
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK_DAY_MONTH
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.PixExtractItemBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum.*
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum.*
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.*
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract

class PixReceiptsAdapter(
    private val receipts: List<PixExtractReceipt>,
    private val listener: PixExtractTabsContract.View
) : RecyclerView.Adapter<PixReceiptsAdapter.PixReceiptsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixReceiptsViewHolder {
        val binding = PixExtractItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PixReceiptsViewHolder(binding, parent.context, listener)
    }

    override fun onBindViewHolder(holder: PixReceiptsViewHolder, position: Int) {
        holder.bind(receipts[position])
    }

    override fun getItemCount(): Int = receipts.size

    class PixReceiptsViewHolder(
        val binding: PixExtractItemBinding,
        val context: Context,
        val listener: PixExtractTabsContract.View
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(receipt: PixExtractReceipt?) {
            binding.container.setOnClickListener {
                listener.showDetails(receipt)
            }
            configureTransactionType(receipt)

            binding.dateOfTransaction.text =
                if (isAScheduledTransaction(receipt) || isAScheduledCancelledOrFailedTransaction(
                        receipt
                    )
                )
                    configureDate(receipt?.schedulingDate)
                else
                    configureDate(receipt?.transactionDate)
        }

        private fun configureDate(date: String?) =
            date?.clearDate()?.formatterDate(LONG_TIME_NO_UTC, SIMPLE_DT_FORMAT_MASK_DAY_MONTH)

        private fun configureTransactionType(receipt: PixExtractReceipt?) {
            when (receipt?.transactionType) {
                TRANSFER_DEBIT.name -> setupSentTransaction(
                    receipt
                )
                TRANSFER_CREDIT.name -> setupReceivedTransaction(
                    receipt
                )
                SCHEDULE_DEBIT.name -> setupSentSchedule(
                    receipt
                )
                REVERSAL_DEBIT.name, REVERSAL_CREDIT.name -> setupReversals(
                    receipt
                )
            }
        }

        private fun setupReversals(receipt: PixExtractReceipt?) {
            val wasReceived = receipt?.transactionType == REVERSAL_CREDIT.name

            when (receipt?.transactionStatus) {
                REVERSAL_EXECUTED.name, NOT_EXECUTED.name, PENDING.name -> {
                    setupView(
                        receipt = receipt,
                        title = if (wasReceived) R.string.pix_received_reversal else R.string.pix_sent_reversal,
                        icon = if (wasReceived) R.drawable.ic_pix_extract_transferencia_recebida else R.drawable.ic_pix_extract_transferencia_enviada
                    )
                }
            }

            if (wasReceived) {
                binding.valueOfTransaction.text = receipt?.finalAmount?.toPtBrRealString()
                binding.nameOfOperator.text = receipt?.payeeName
            } else {
                binding.valueOfTransaction.text = context.getString(
                    R.string.pix_negative_value,
                    receipt?.finalAmount?.toPtBrRealString()
                )
                binding.nameOfOperator.text = receipt?.payerName
            }
        }

        private fun setupSentTransaction(receipt: PixExtractReceipt?) {
            when (receipt?.pixType) {
                WITHDRAWAL.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_sent_withdraw,
                        R.drawable.ic_pix_extract_saque
                    )
                }

                CHANGE.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_sent_change,
                        R.drawable.ic_pix_extract_pagamento_qrcode
                    )
                }

                TRANSFER.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_payment_transfer,
                        R.drawable.ic_pix_extract_pagamento_qrcode
                    )

                    setupView(
                        receipt,
                        PixTransferTypeEnum.CHAVE.name,
                        PixTransferTypeEnum.MANUAL.name,
                        R.string.pix_sent_transfer,
                        R.drawable.ic_pix_extract_transferencia_enviada
                    )
                }
            }

            binding.valueOfTransaction.text = context.getString(
                R.string.pix_negative_value,
                receipt?.finalAmount?.toPtBrRealString()
            )
            binding.nameOfOperator.text = receipt?.payerName
        }

        private fun setupReceivedTransaction(receipt: PixExtractReceipt?) {
            when (receipt?.pixType) {
                WITHDRAWAL.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_received_withdraw,
                        R.drawable.ic_pix_extract_recebido_qrcode
                    )
                }

                CHANGE.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_received_change,
                        R.drawable.ic_pix_extract_recebido_qrcode
                    )
                }

                TRANSFER.name -> {
                    setupView(
                        receipt,
                        PixTransferTypeEnum.QR_CODE_DINAMICO.name,
                        PixTransferTypeEnum.QR_CODE_ESTATICO.name,
                        R.string.pix_qr_code_received_payment_transfer,
                        R.drawable.ic_pix_extract_transferencia_recebida
                    )

                    setupView(
                        receipt,
                        PixTransferTypeEnum.CHAVE.name,
                        PixTransferTypeEnum.MANUAL.name,
                        R.string.pix_received_transfer,
                        R.drawable.ic_pix_extract_transferencia_recebida
                    )
                }
            }

            binding.valueOfTransaction.text = receipt?.finalAmount?.toPtBrRealString()
            binding.nameOfOperator.text = receipt?.payeeName
        }

        private fun setupSentSchedule(receipt: PixExtractReceipt?) {
            receipt?.also {
                setupView(
                    receipt = receipt,
                    title = R.string.text_pix_extract_card_scheduled,
                    icon = R.drawable.ic_pix_extract_agendado
                )
            }

            binding.valueOfTransaction.text = context.getString(
                R.string.pix_negative_value,
                receipt?.finalAmount?.toPtBrRealString()
            )
            binding.nameOfOperator.text = receipt?.payeeName
        }

        private fun showFinalExtractDetails(
            receipt: PixExtractReceipt,
            titleText: String,
            icon: Int
        ) {
            binding.apply {
                typeOfTransaction.text = titleText

                when {
                    isAnExecutedTransaction(receipt) || isAScheduledTransaction(receipt) || isAnExecutedReversalTransaction(
                        receipt
                    ) -> {
                        imgExtractStatus.setBackgroundResource(icon)
                    }
                    isAPendingTransaction(receipt) -> {
                        imgExtractStatus.setBackgroundResource(R.drawable.ic_pix_pending)
                    }
                    else -> {
                        imgExtractStatus.setBackgroundResource(R.drawable.ic_pix_extract_canceled)

                        strokeText(
                            binding.typeOfTransaction,
                            binding.valueOfTransaction,
                            binding.nameOfOperator
                        )
                    }
                }
            }
        }

        private fun strokeText(vararg textView: TextView?) {
            textView.forEach {
                it?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
        }

        private fun isAnExecutedTransaction(receipt: PixExtractReceipt?): Boolean {
            return when (receipt?.transactionStatus) {
                NOT_EXECUTED.name -> false
                EXECUTED.name -> true
                else -> false
            }
        }

        private fun isAPendingTransaction(receipt: PixExtractReceipt?) =
            receipt?.transactionStatus == PENDING.name

        private fun isAScheduledTransaction(receipt: PixExtractReceipt?) =
            receipt?.transactionStatus == SCHEDULED.name

        private fun isAScheduledCancelledOrFailedTransaction(receipt: PixExtractReceipt?) =
            receipt?.transactionStatus == CANCELLED.name || receipt?.transactionStatus == FAILED.name

        private fun isAReversalTransaction(receipt: PixExtractReceipt?) =
            receipt?.transactionType == REVERSAL_CREDIT.name || receipt?.transactionType == REVERSAL_DEBIT.name

        private fun isAnExecutedReversalTransaction(receipt: PixExtractReceipt?) =
            receipt?.transactionStatus == REVERSAL_EXECUTED.name

        private fun setupView(
            receipt: PixExtractReceipt,
            firstTransferType: String = EMPTY,
            secondTransferType: String = EMPTY,
            @StringRes title: Int,
            @DrawableRes icon: Int
        ) {
            if (receipt.transferType == firstTransferType ||
                receipt.transferType == secondTransferType ||
                isAScheduledTransaction(receipt) ||
                isAScheduledCancelledOrFailedTransaction(receipt) ||
                isAReversalTransaction(receipt)
            ) {
                showFinalExtractDetails(
                    receipt,
                    context.getString(title),
                    icon
                )
            }
        }
    }
}