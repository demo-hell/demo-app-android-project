package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer.ratesDetails.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutPosVirtualAccreditationRatesDetailsCardItemBinding
import br.com.mobicare.cielo.posVirtual.domain.model.RateUI

class PosVirtualAccreditationRatesDetailsBrandRateViewHolder(
    private val binding: LayoutPosVirtualAccreditationRatesDetailsCardItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(rateUI: RateUI) {
        binding.apply {
            tvNumberPlots.text = rateUI.label
            tvRate.text = rateUI.rate
        }
    }

}