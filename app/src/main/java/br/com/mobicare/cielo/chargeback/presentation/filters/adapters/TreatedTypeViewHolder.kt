package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterDisputeStatus
import br.com.mobicare.cielo.databinding.ItemFilterChargebackDisputeStatusBinding
import org.androidannotations.annotations.res.ColorRes
import org.androidannotations.annotations.res.DrawableRes

class TreatedTypeViewHolder(private val binding: ItemFilterChargebackDisputeStatusBinding):
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ChargebackFilterDisputeStatus) {
        binding.statusNameTextView.text = item.chargebackDisputeStatusName
        changeColorState(item, binding)
        binding.root.setOnClickListener {
            item.isSelected = item.isSelected.not()
            changeColorState(item, binding)
            setupContentDescription(item, binding)
        }
    }

    private fun changeColorState(
        item: ChargebackFilterDisputeStatus,
        binding: ItemFilterChargebackDisputeStatusBinding
    ) {
        with(binding) {
            val backgroundColor =
                if (item.isSelected) ColorStateStatus.SELECTED.backgroundResource else ColorStateStatus.UNSELECTED.backgroundResource
            val textColor =
                root.resources.getColor(if (item.isSelected) ColorStateStatus.SELECTED.textColor else ColorStateStatus.UNSELECTED.textColor)

            statusNameTextView.setBackgroundResource(backgroundColor)
            statusNameTextView.setTextColor(textColor)
        }
    }

    private fun setupContentDescription(
        item: ChargebackFilterDisputeStatus,
        binding: ItemFilterChargebackDisputeStatusBinding
    ) {
        if (item.isSelected) {
            binding.root.contentDescription = binding.root.context.getString(
                R.string.chargeback_filter_selected_accessibility, item.chargebackDisputeStatusName
            )
        }
    }
}
enum class ColorStateStatus(@DrawableRes  val  backgroundResource: Int, @ColorRes val textColor: Int) {
    SELECTED(R.drawable.shape_filled_brand400_rounded_corner,R.color.cloud_0),
    UNSELECTED(R.drawable.shape_transparent_rounded_corner_stroke_cloud_300_dp,R.color.cloud_600);
}