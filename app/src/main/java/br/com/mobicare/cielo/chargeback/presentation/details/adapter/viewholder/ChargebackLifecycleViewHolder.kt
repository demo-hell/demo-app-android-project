package br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.databinding.ItemChargebackLifecycleBinding

class ChargebackLifecycleViewHolder(
    private val binding: ItemChargebackLifecycleBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val context = binding.root.context

    fun bind(lifecycle: Lifecycle) {
        binding.apply {
            tvLifecycleAction.text = lifecycle.action
            tvLifecycleActionDate.text = context.getString(
                R.string.chargeback_lifecycle_action_date,
                lifecycle.actionDate?.convertToBrDateFormat()
            )
        }
    }

}