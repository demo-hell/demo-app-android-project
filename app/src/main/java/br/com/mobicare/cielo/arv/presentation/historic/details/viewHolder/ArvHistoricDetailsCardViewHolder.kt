package br.com.mobicare.cielo.arv.presentation.historic.details.viewHolder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.arv.presentation.historic.details.model.ArvHistoricDetailsCardModel
import br.com.mobicare.cielo.databinding.LayoutCardGeneralInformationBinding

class ArvHistoricDetailsCardViewHolder(
        private val binding: LayoutCardGeneralInformationBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(infoCard: ArvHistoricDetailsCardModel) {
        binding.apply {
            tvLabelOne.text = infoCard.labelOne
            tvLabelTwo.text = infoCard.labelTwo
            tvValueOne.text = infoCard.valueOne
            tvValueTwo.text = infoCard.valueTwo
            infoCard.iconOne?.let { ivIconOne.setImageResource(it) }
            infoCard.iconTwo?.let { ivIconTwo.setImageResource(it) }
        }
    }

    fun setContentDescriptionCard(description: String) {
        binding.root.contentDescription = description
    }

}