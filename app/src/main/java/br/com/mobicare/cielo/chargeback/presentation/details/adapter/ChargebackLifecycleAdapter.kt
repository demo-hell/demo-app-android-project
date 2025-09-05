package br.com.mobicare.cielo.chargeback.presentation.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder.ChargebackLifecycleViewHolder
import br.com.mobicare.cielo.databinding.ItemChargebackLifecycleBinding

class ChargebackLifecycleAdapter(
    private val lifecycleList: List<Lifecycle>
) : RecyclerView.Adapter<ChargebackLifecycleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargebackLifecycleViewHolder {
        return ItemChargebackLifecycleBinding.inflate(
            LayoutInflater.from(parent.context), parent,false
        ).let {
            ChargebackLifecycleViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ChargebackLifecycleViewHolder, position: Int) {
        holder.bind(lifecycleList[position])
    }

    override fun getItemCount() = lifecycleList.size

}