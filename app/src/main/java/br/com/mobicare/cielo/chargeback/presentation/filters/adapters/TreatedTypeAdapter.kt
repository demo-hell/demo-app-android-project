package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterDisputeStatus
import br.com.mobicare.cielo.databinding.ItemFilterChargebackDisputeStatusBinding

class TreatedTypeAdapter: RecyclerView.Adapter<TreatedTypeViewHolder>() {

    private var statusItems: MutableList<ChargebackFilterDisputeStatus> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreatedTypeViewHolder {
        val binding = ItemFilterChargebackDisputeStatusBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false
        )

        return TreatedTypeViewHolder(binding)
    }

    override fun getItemCount(): Int  = statusItems.size

    override fun onBindViewHolder(holder: TreatedTypeViewHolder, position: Int) {
        holder.bind(statusItems[position])
    }

    fun updateAdapter(items: List<ChargebackFilterDisputeStatus>) {
        statusItems.clear()
        statusItems.addAll(items)
        notifyDataSetChanged()
    }

    fun getUserSelectedTreatedType(): ArrayList<Int> {
        return statusItems.filter { status -> status.isSelected }.map { it.chargebackDisputeStatusCode } as ArrayList<Int>
    }

}