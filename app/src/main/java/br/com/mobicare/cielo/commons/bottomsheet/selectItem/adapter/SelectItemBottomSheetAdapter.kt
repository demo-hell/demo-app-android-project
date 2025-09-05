package br.com.mobicare.cielo.commons.bottomsheet.selectItem.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.model.RowSelectItemModel
import br.com.mobicare.cielo.commons.bottomsheet.selectItem.viewHolder.SelectItemBottomSheetViewHolder
import br.com.mobicare.cielo.databinding.LayoutRowSelectItemBottomSheetBinding

class SelectItemBottomSheetAdapter(
    private val items: List<RowSelectItemModel>,
    private val closeBottomSheet: () -> Unit,
) : RecyclerView.Adapter<SelectItemBottomSheetViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectItemBottomSheetViewHolder {
        val binding = LayoutRowSelectItemBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectItemBottomSheetViewHolder(binding, parent.context, closeBottomSheet)
    }

    override fun onBindViewHolder(holder: SelectItemBottomSheetViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}