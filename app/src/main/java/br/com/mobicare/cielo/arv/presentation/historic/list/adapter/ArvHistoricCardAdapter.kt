package br.com.mobicare.cielo.arv.presentation.historic.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.presentation.historic.list.viewHolder.ArvHistoricCardViewHolder
import br.com.mobicare.cielo.databinding.LayoutCardHistoricBinding

class ArvHistoricCardAdapter: RecyclerView.Adapter<ArvHistoricCardViewHolder>() {

    private var negotiations: ArrayList<Item> = ArrayList()
    private var onTap: ((Item) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArvHistoricCardViewHolder {
        val item = LayoutCardHistoricBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ArvHistoricCardViewHolder(item, parent.context)
    }

    override fun onBindViewHolder(holder: ArvHistoricCardViewHolder, position: Int) {
        holder.bind(negotiations[position])
        holder.setOnClickListener {
            onTap?.invoke(negotiations[position])
        }
    }

    override fun getItemCount(): Int {
        return negotiations.count()
    }

    fun setNegotiations(list: List<Item>) {
        negotiations.clear()
        negotiations.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnTap(value: ((Item) -> Unit)){
        onTap = value
    }

}