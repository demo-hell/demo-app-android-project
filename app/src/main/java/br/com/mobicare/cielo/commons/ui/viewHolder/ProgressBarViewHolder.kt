package br.com.mobicare.cielo.commons.ui.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutProgressBarBinding
import br.com.mobicare.cielo.extensions.visible

class ProgressBarViewHolder(
    private val binding: LayoutProgressBarBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind() {
        binding.pgLoading.visible()
    }

}