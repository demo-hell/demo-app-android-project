package br.com.mobicare.cielo.mySales.presentation.ui.adapter.sales

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

@SuppressLint("NotifyDataSetChanged")
class SalesAdapter(
    private val clickListener: (Sale) -> Unit,
    private val quickFilter: QuickFilter
): RecyclerView.Adapter<SalesViewHolder>() {

    private var salesList: MutableList<Sale> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val binding = LayoutItemMinhasVendasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SalesViewHolder(binding,clickListener,quickFilter)
    }

    override fun getItemCount(): Int = salesList.size


    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        holder.bind(salesList[position])
    }

    fun updateAdapter(sales: List<Sale>) {
        salesList.clear()
        salesList.addAll(sales)
        notifyDataSetChanged()
    }

    fun addMoreData(sales: List<Sale>){
        val currentSize = salesList.size
        salesList = salesList.plus(sales).toMutableList()
        notifyItemRangeInserted(currentSize, salesList.size)
    }
}