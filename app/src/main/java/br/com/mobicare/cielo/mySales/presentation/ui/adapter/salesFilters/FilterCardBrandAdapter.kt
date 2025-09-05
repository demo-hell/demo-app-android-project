package br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemCardBrandBinding
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable


@SuppressLint("NotifyDataSetChanged")
class FilterCardBrandAdapter: RecyclerView.Adapter<FilterCardBrandViewHolder>() {

    private var cardBrandItems: MutableList<ItemSelectable<CardBrand>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterCardBrandViewHolder {
        val binding = LayoutItemCardBrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterCardBrandViewHolder(binding)
    }

    override fun getItemCount(): Int  = cardBrandItems.size

    override fun onBindViewHolder(holder: FilterCardBrandViewHolder, position: Int) {
        holder.bind(cardBrandItems[position])
    }

    fun updateAdapter(cardItems: List<ItemSelectable<CardBrand>>){
        cardBrandItems.clear()
        cardBrandItems.addAll(cardItems)
        notifyDataSetChanged()
    }
}