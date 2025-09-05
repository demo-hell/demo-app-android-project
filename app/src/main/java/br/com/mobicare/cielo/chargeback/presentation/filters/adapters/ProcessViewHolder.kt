package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterCardBrand
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterProcess
import br.com.mobicare.cielo.databinding.ItemFilterChargebackProcessBinding
import org.androidannotations.annotations.res.ColorRes
import org.androidannotations.annotations.res.DrawableRes

class ProcessViewHolder(private val binding: ItemFilterChargebackProcessBinding):
    RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChargebackFilterProcess){
            binding.processNameTextView.text = item.chargebackProcessName
            changeColorState(item,binding)
            binding.root.setOnClickListener {
                item.isSelected = item.isSelected.not()
                changeColorState(item,binding)
                setupContentDescription(item,binding)
            }
        }
    private fun changeColorState(item: ChargebackFilterProcess, binding: ItemFilterChargebackProcessBinding){
        if(item.isSelected){
            binding.processNameTextView.setBackgroundResource(ColorState.SELECTED.backgroundResource)
            binding.processNameTextView.setTextColor(binding.root.resources.getColor(ColorState.SELECTED.textColor))
        }else {
            binding.processNameTextView.setBackgroundResource(ColorState.UNSELECTED.backgroundResource)
            binding.processNameTextView.setTextColor(binding.root.resources.getColor(ColorState.UNSELECTED.textColor))
        }
    }

    private fun setupContentDescription(item: ChargebackFilterProcess, binding: ItemFilterChargebackProcessBinding) {
        if(item.isSelected){
            binding.root.contentDescription = binding.root.context.getString(
                R.string.chargeback_filter_selected_accessibility,item.chargebackProcessName)
        }

    }
}


enum class ColorState(@DrawableRes  val  backgroundResource: Int, @ColorRes val textColor: Int) {
    SELECTED(R.drawable.shape_filled_brand400_rounded_corner,R.color.cloud_0),
    UNSELECTED(R.drawable.shape_transparent_rounded_corner_stroke_cloud_300_dp,R.color.cloud_600);
}
