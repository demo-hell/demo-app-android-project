package br.com.mobicare.cielo.pix.ui.extract.detail

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.RefundHistoryItemBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.pix.domain.ReceiptItem

class PixReversalHistoryAdapter(val context: Context) :
    RecyclerView.Adapter<PixReversalHistoryAdapter.PixRefundHistoryViewHolder>() {

    private var receipts: ArrayList<ReceiptItem> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun populateList(response: List<ReceiptItem>) {
        receipts.clear()
        receipts.addAll(response)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PixRefundHistoryViewHolder {
        val binding = RefundHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PixRefundHistoryViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: PixRefundHistoryViewHolder, position: Int) {
        holder.bind(receipts[position].transactionDate, receipts[position].finalAmount)
    }

    override fun getItemCount(): Int {
        return receipts.size
    }

    class PixRefundHistoryViewHolder(
        val binding: RefundHistoryItemBinding,
        val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(refundDate: String?, refundAmount: Double?) {
            binding.refundDate.text = refundDate?.clearDate()?.convertToBrDateFormat()
            binding.refundAmount.text = refundAmount?.toPtBrRealString()
        }
    }
}