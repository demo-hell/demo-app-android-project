package br.com.mobicare.cielo.commons.bottomsheet.selectItem.viewHolder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.model.RowSelectItemModel
import br.com.mobicare.cielo.databinding.LayoutRowSelectItemBottomSheetBinding
import br.com.mobicare.cielo.extensions.visible

class SelectItemBottomSheetViewHolder(
    private val binding: LayoutRowSelectItemBottomSheetBinding,
    private val context: Context,
    private val closeBottomSheet: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(row: RowSelectItemModel) {
        binding.apply {
            txtLabel.text = context.getString(row.label)
            row.iconStart?.let {
                icStart.visible()
                icStart.setImageResource(it)
            }
            row.iconEnd?.let {
                icEnd.visible()
                icEnd.setImageResource(it)
            }
            rowContainer.setOnClickListener {
                row.onClick()
                closeBottomSheet()
            }
        }
    }

}