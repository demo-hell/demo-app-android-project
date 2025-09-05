package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemPixInfringementReasonBinding
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixEligibilityInfringementResponse

class PixInfringementSituationViewHolder(
    private val binding: ItemPixInfringementReasonBinding,
    private val onClick: (PixEligibilityInfringementResponse.Situation) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(situation: PixEligibilityInfringementResponse.Situation) {
        binding.tvReason.text = situation.description
        binding.root.setOnClickListener {
            onClick(situation)
        }
    }

}