package br.com.mobicare.cielo.mySales.presentation.ui.adapter.saleDetail

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemExtratoDetalheBinding

class SaleDetailViewHolder(
    private val binding: ItemExtratoDetalheBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<String,String>){
            binding.textviewItemExtratoDetalheKey.text = item.first
            binding.textviewItemExtratoDetalheValue.text = item.second
        }
}