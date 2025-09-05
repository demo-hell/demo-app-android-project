package br.com.mobicare.cielo.pagamentoLink.orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order

class OrderAdapter(private val orders: List<Order>,
                   private val onClick: (Order) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.link_list_order_item, parent, false).let {
                    OrderHolder(it, onClick)
                }
    }

    override fun getItemCount() = orders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as OrderHolder).bindItem(orders[position])
    }
}