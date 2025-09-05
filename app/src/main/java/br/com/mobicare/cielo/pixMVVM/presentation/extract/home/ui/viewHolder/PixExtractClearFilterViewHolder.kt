package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixExtractClearFilterBinding
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixExtractFilterListener

class PixExtractClearFilterViewHolder(
    private val binding: LayoutPixExtractClearFilterBinding,
    private val listener: PixExtractFilterListener
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context

    fun bind() {
        binding.tvClearFilter.apply {
            contentDescription =
                context.getString(R.string.accessibility_button_description_pattern, text)
            setOnClickListener {
                listener.onClickClearFilter()
            }
        }
    }

}