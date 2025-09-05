package br.com.mobicare.cielo.mySales.presentation.ui.adapter.consolidatedSales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.ItemExtratoListBinding
import br.com.mobicare.cielo.mySales.data.model.SaleHistory


class ConsolidatedSalesAdapter(private val clickListener: (String) -> Unit):
    RecyclerView.Adapter<ConsolidatedSalesViewHolder>() {

    private var salesHistoryList: MutableList<SaleHistory>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsolidatedSalesViewHolder {
        val binding = ItemExtratoListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConsolidatedSalesViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int  = salesHistoryList?.size ?: ZERO

    override fun onBindViewHolder(holder: ConsolidatedSalesViewHolder, position: Int) {
        salesHistoryList?.get(position)?.let {
            holder.bind(it)
        }
    }

    fun updateAdapter(sales: List<SaleHistory>) {
        salesHistoryList = mutableListOf()
        salesHistoryList?.addAll(sales)
        notifyDataSetChanged()
    }

}