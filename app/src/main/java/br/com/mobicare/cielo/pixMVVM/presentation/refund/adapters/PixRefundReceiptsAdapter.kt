package br.com.mobicare.cielo.pixMVVM.presentation.refund.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.asNegative
import br.com.mobicare.cielo.commons.utils.parseToString
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrWithNegativeRealString
import br.com.mobicare.cielo.databinding.ItemPixRefundReceiptsBinding
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundReceipts

class PixRefundReceiptsAdapter(
    private val items: List<PixRefundReceipts.ReceiptItem>
) : RecyclerView.Adapter<PixRefundReceiptsAdapter.PixRefundReceiptsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixRefundReceiptsViewHolder =
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_pix_refund_receipts, parent, false)
            .let { PixRefundReceiptsViewHolder(it) }

    override fun onBindViewHolder(holder: PixRefundReceiptsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class PixRefundReceiptsViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemPixRefundReceiptsBinding.bind(itemView)

        fun bind(receiptItem: PixRefundReceipts.ReceiptItem) {
            binding.apply {
                tvSubtitle.text = receiptItem.transactionDate?.parseToString(TRANSACTION_DATE_FORMAT)
                tvAmount.text = receiptItem.finalAmount?.asNegative()?.toPtBrWithNegativeRealString()
                flIcon.setCustomDrawable {
                    solidColor = R.color.danger_100
                    radius = R.dimen.dimen_4dp
                }
            }
        }
    }

    companion object {
        private const val TRANSACTION_DATE_FORMAT = "dd/MM/yyyy HH:mm"
    }

}