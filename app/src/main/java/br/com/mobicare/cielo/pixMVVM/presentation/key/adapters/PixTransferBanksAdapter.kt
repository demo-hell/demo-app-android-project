package br.com.mobicare.cielo.pixMVVM.presentation.key.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemPixTransferBankBinding
import br.com.mobicare.cielo.extensions.removeAccents
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank

@SuppressLint("NotifyDataSetChanged")
class PixTransferBanksAdapter(
    private val onItemTap: (PixTransferBank) -> Unit
) : RecyclerView.Adapter<PixTransferBanksAdapter.PixTransferBanksViewHolder>() {

    private var items = emptyList<PixTransferBank>()
    private var filteredItems = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PixTransferBanksViewHolder =
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_pix_transfer_bank, parent, false)
            .let { PixTransferBanksViewHolder(it) }

    override fun onBindViewHolder(holder: PixTransferBanksViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount() = filteredItems.size

    fun setItems(newItems: List<PixTransferBank>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun filter(input: String) {
        filteredItems = if (input.isNotBlank()) getFilteredItems(input) else items
        notifyDataSetChanged()
    }

    private fun getFilteredItems(text: String) = items.filter {
        it.code.toString() == text
                || it.name.removeAccents().contains(text.removeAccents(), ignoreCase = true)
    }

    inner class PixTransferBanksViewHolder(itemView: View) : ViewHolder(itemView) {
        private val binding = ItemPixTransferBankBinding.bind(itemView)

        fun bind(bank: PixTransferBank) {
            binding.apply {
                tvContent.text = bank.codeAndName
                root.setOnClickListener { onItemTap(bank) }
            }
        }
    }

}