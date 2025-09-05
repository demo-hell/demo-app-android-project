package br.com.mobicare.cielo.chargeback.presentation.filters.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackFilterProcess
import br.com.mobicare.cielo.databinding.ItemFilterChargebackProcessBinding

class ProcessAdapter: RecyclerView.Adapter<ProcessViewHolder>() {

    private var processItems: MutableList<ChargebackFilterProcess> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessViewHolder {
        val binding = ItemFilterChargebackProcessBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,false
        )

        return ProcessViewHolder(binding)
    }

    override fun getItemCount(): Int  = processItems.size


    override fun onBindViewHolder(holder: ProcessViewHolder, position: Int) {
       holder.bind(processItems[position])
    }


    fun updateAdapter(items: List<ChargebackFilterProcess>) {
        processItems.clear()
        processItems.addAll(items)
        notifyDataSetChanged()
    }

    fun getUserSelectedProcess(): ArrayList<Int> {
        val selected = processItems.filter { process -> process.isSelected }
        val arrayOfIDs: ArrayList<Int> = arrayListOf()
        selected.forEach { arrayOfIDs.add(it.chargebackProcessCode) }
        return arrayOfIDs
    }
}