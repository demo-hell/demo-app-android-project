package br.com.mobicare.cielo.pagamentoLink.orders.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import kotlinx.android.synthetic.main.link_list_order_item.view.*

class OrderHolder(
    itemView: View,
    private val onClick: (Order) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    fun bindItem(item: Any) {
        val order = item as Order
        itemView.textViewName.text = "${order.customer?.name}"
        itemView.textViewStatus.text =
            if (!order.shipping?.statusDescription.isNullOrEmpty()) order.shipping?.statusDescription
            else order.payment?.statusDescription
        itemView.setOnClickListener { onClick.invoke(order) }
    }
}