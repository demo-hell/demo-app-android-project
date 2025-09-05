package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterCardBrand
import br.com.mobicare.cielo.databinding.ItemFilterCardbrandBinding

class CardBrandAdapter: RecyclerView.Adapter<CardBrandViewHolder>() {

    private var cardBrandItems: MutableList<ChargebackFilterCardBrand> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardBrandViewHolder {
        val binding = ItemFilterCardbrandBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardBrandViewHolder(binding)
    }

    override fun getItemCount(): Int =  cardBrandItems.size
    override fun onBindViewHolder(holder: CardBrandViewHolder, position: Int) {
        holder.bind(cardBrandItems[position])
    }

    fun updateAdapter(cardItems: List<ChargebackFilterCardBrand>) {
        cardBrandItems.clear()
        cardBrandItems.addAll(cardItems)
        notifyDataSetChanged()
    }

    fun getUserSelectedCardBrands(): ArrayList<Int> {
        val selected = cardBrandItems.filter { cardBrand -> cardBrand.isSelected }
        val arrayOfIDs: ArrayList<Int> = arrayListOf()
        selected.forEach { arrayOfIDs.add(it.brandCode) }
        return arrayOfIDs

    }

}