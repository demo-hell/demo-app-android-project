package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterCardBrand
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.databinding.ItemFilterCardbrandBinding

class CardBrandViewHolder(private val binding: ItemFilterCardbrandBinding):
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChargebackFilterCardBrand ){
            loadCardBrandImageByCode(item.brandCode,binding.cardBrandImage)
            changeBorder(item,binding)
            binding.root.setOnClickListener {
                item.isSelected = item.isSelected.not()
                changeBorder(item, binding)
                setupContentDescription(item, binding)

            }
        }

    private fun loadCardBrandImageByCode(code: Int, imageView: ImageView){
        BrandCardHelper.getUrlBrandImageByCode(code).let{ uri ->
            ImageUtils.loadImage(imageView,uri)
        }
    }

    private fun changeBorder(item: ChargebackFilterCardBrand, binding: ItemFilterCardbrandBinding) {
        binding.borderCardBrandImage.setBackgroundResource(
            if(item.isSelected) R.drawable.rounded_border_blue_sales
            else R.drawable.rounded_border_gray
        )
    }


    private fun setupContentDescription(item: ChargebackFilterCardBrand, binding: ItemFilterCardbrandBinding){
        if(item.isSelected){
            binding.root.contentDescription = binding.root.context.getString(
                R.string.chargeback_filter_selected_accessibility,item.brandName)
        }
    }
}