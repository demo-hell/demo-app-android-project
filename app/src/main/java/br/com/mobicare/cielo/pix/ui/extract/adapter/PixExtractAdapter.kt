package br.com.mobicare.cielo.pix.ui.extract.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.CardExtractPixBinding
import br.com.mobicare.cielo.pix.domain.PixExtractItem
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract

class PixExtractAdapter(private val listener: PixExtractTabsContract.View) :
    RecyclerView.Adapter<PixExtractAdapter.PixExtractViewHolder>() {

    private var extractItems: ArrayList<PixExtractItem> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun populateList(response: PixExtractResponse) {
        extractItems.clear()
        response.items?.let { extractItems.addAll(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixExtractViewHolder {
        val binding = CardExtractPixBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PixExtractViewHolder(binding, parent.context, listener)
    }

    override fun onBindViewHolder(holder: PixExtractViewHolder, position: Int) {
        holder.bind(extractItems[position])
    }

    override fun getItemCount(): Int = extractItems.size

    class PixExtractViewHolder(
        val binding: CardExtractPixBinding,
        val context: Context,
        val listener: PixExtractTabsContract.View
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(extract: PixExtractItem) {
            binding.monthTransaction.text = extract.title
            binding.recyclerReceipts.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = extract.receipts?.let { PixReceiptsAdapter(it, listener) }
            }
        }
    }
}