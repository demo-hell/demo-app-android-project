package br.com.mobicare.cielo.commons.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.imageUtils.ImageUtils
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.filter.model.CardBrand
import br.com.mobicare.cielo.databinding.LayoutItemCardBrandBinding
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable

class CardBrandAdapter: RecyclerView.Adapter<CardBrandAdapter.CardBrandViewHolder>() {

    private var cardBrandItems: MutableList<ItemSelectable<CardBrand?>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardBrandViewHolder {
        val binding = LayoutItemCardBrandBinding.inflate(
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

    fun updateAdapter(cardItems: List<ItemSelectable<CardBrand?>>) {
        cardBrandItems.clear()
        cardBrandItems.addAll(cardItems)
        notifyDataSetChanged()
    }

    fun getUserSelectedCardBrands(): ArrayList<Int> {
        val selected = cardBrandItems.filter { cardBrand -> cardBrand.isSelected }
        val arrayOfIDs: ArrayList<Int> = arrayListOf()
        selected.forEach { it.data?.value?.let { it1 -> arrayOfIDs.add(it1.toInt()) } }
        return arrayOfIDs

    }

    inner class CardBrandViewHolder(private val binding: LayoutItemCardBrandBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemSelectable<CardBrand?>){
            item.data?.value?.let { loadCardBrandImageByCode(it.toInt(),binding.cardBrandImage) }
            changeBorder(item,binding)
            binding.root.setOnClickListener {
                item.isSelected = item.isSelected.not()
                changeBorder(item, binding)
            }
        }

        private fun loadCardBrandImageByCode(code: Int, imageView: ImageView){
            BrandCardHelper.getUrlBrandImageByCode(code).let{ uri ->
                ImageUtils.loadImage(imageView,uri)
            }
        }

        private fun changeBorder(item: ItemSelectable<CardBrand?>, binding: LayoutItemCardBrandBinding) {
            binding.borderCardBrandImage.setBackgroundResource(
                if(item.isSelected) R.drawable.rounded_border_blue_sales
                else R.drawable.rounded_border_gray
            )
        }
    }
}