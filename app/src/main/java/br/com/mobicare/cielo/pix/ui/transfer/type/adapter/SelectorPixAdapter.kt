package br.com.mobicare.cielo.pix.ui.transfer.type.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemPixSelectorBottomSheetBinding
import br.com.mobicare.cielo.pix.model.PixKeyType
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorContract

class SelectorPixAdapter(
    private val items: List<PixKeyType>,
    val listener: PixSelectorContract.View
) : RecyclerView.Adapter<SelectorPixGroupHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectorPixGroupHolder {
        val binding = ItemPixSelectorBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectorPixGroupHolder(binding, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SelectorPixGroupHolder, position: Int) {
        holder.bind(items[position])
    }
}