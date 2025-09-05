package br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.databinding.LayoutItemCardBrandBinding
import br.com.mobicare.cielo.minhasVendas.activities.VENDAS_CANCELADAS_CATEGORY
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable
import br.com.mobicare.cielo.mySales.presentation.utils.brand

class FilterCardBrandViewHolder(
    private val binding: LayoutItemCardBrandBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ItemSelectable<CardBrand>) {
        loadCardBrandImageByCode(item.data,binding.cardBrandImage)
        changeBorder(item,binding)
        setupContentDescription(item,binding)
        binding.root.setOnClickListener {
            item.isSelected = item.isSelected.not()
            changeBorder(item, binding)
            setupContentDescription(item, binding)
            gaSendButtonCancelCheckboxBandeira(item.data.name)
        }
    }

    private fun loadCardBrandImageByCode(item: CardBrand, imageView: ImageView){
        BrandCardHelper.getUrlBrandImageByCode(item.code)?.let { itUrl ->
            ImageUtils.loadImage(imageView,itUrl)
        }
    }

    private fun changeBorder(item: ItemSelectable<CardBrand>, binding: LayoutItemCardBrandBinding) {
        binding.borderCardBrandImage.setBackgroundResource(if(item.isSelected) R.drawable.rounded_border_blue_sales else R.drawable.rounded_border_gray)
    }

    private fun setupContentDescription(item: ItemSelectable<CardBrand>, binding: LayoutItemCardBrandBinding){
        binding.root.contentDescription = binding.root.context.getString(
            if(item.isSelected) R.string.description_focused_selected_flag_card else R.string.description_focused_unselected_flag_card,
            item.data.name
        )
    }

    private fun gaSendButtonCancelCheckboxBandeira(name: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, VENDAS_CANCELADAS_CATEGORY),
            action = listOf(Action.MODAL, Action.MAIS_FILTROS),
            label = listOf(Label.BOTAO, brand, name)
        )
    }
}