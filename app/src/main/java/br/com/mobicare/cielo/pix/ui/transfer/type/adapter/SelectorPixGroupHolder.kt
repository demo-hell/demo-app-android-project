package br.com.mobicare.cielo.pix.ui.transfer.type.adapter

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemPixSelectorBottomSheetBinding
import br.com.mobicare.cielo.pix.model.PixKeyType
import br.com.mobicare.cielo.pix.ui.transfer.type.PixSelectorContract

class SelectorPixGroupHolder(
    private val binding: ItemPixSelectorBottomSheetBinding,
    private val listener: PixSelectorContract.View
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(key: PixKeyType) {
        binding?.tvTypeKeySelector?.text = key.title
        binding?.ivTypeKeySelector?.setBackgroundResource(key.image)

        binding?.root?.setOnClickListener {
            listener.onSelectedKeyType(key.type)
        }
    }
}