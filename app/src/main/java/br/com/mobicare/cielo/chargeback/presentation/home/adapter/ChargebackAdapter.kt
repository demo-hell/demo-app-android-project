package br.com.mobicare.cielo.chargeback.presentation.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.databinding.ItemChargebackBinding

class ChargebackAdapter(
    private val onItemClicked: (Chargeback) -> Unit
) : RecyclerView.Adapter<ChargebackItemViewHolder>() {

    private val chargebackList: MutableList<Chargeback> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargebackItemViewHolder {
        val item = ItemChargebackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChargebackItemViewHolder(item)
    }

    override fun getItemCount(): Int {
        return chargebackList.count()
    }

    override fun onBindViewHolder(holder: ChargebackItemViewHolder, position: Int) {
        holder.bind(chargebackList[position])
        holder.setOnItemClicked(onItemClicked)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun update(items: List<Chargeback>){
        chargebackList.addAll(items)
        notifyDataSetChanged()
    }

    fun clear(){
        chargebackList.clear()
        notifyDataSetChanged()
    }
}