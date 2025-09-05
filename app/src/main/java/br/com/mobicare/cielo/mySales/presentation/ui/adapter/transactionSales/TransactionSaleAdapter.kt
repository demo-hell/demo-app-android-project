package br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.Sale

class TransactionSaleAdapter(
    private val clickListener: (Sale) -> Unit
): RecyclerView.Adapter<TransactionSaleViewHolder>() {

    private var salesList: MutableList<Sale> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionSaleViewHolder {
        val binding = LayoutItemMinhasVendasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
               false
        )
        return TransactionSaleViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = salesList.size


    override fun onBindViewHolder(holder: TransactionSaleViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    fun setSales(list: MutableList<Sale>){
        salesList.clear()
        salesList.addAll(list)
        notifyDataSetChanged()
    }

    fun addSales(sales: List<Sale>){
        salesList.addAll(sales)
        notifyDataSetChanged()
    }

}