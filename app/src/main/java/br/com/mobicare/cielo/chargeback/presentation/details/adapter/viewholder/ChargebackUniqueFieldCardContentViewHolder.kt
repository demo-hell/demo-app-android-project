package br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContent
import br.com.mobicare.cielo.databinding.ItemChargebackUniqueFieldInfoBinding

class ChargebackUniqueFieldCardContentViewHolder(private val binding: ItemChargebackUniqueFieldInfoBinding):
RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ChargebackInfoContent) {
        setCardIcon(binding.ivChargebackUniqueInfoCardIcon,item.firstField.contentIcon,item.firstField.labelText)
        binding.tvChargebackUniqueInfoCardLabel.text = item.firstField.labelText
        binding.tvChargebackUniqueInfoCardContent.text = item.firstField.contentText.orEmpty()
    }

    private fun setCardIcon(imageView: ImageView, iconId: Int?, description: String){
        imageView.apply {
            if (iconId != null) {
                setImageResource(iconId)
            }
            contentDescription = description
        }
    }
}