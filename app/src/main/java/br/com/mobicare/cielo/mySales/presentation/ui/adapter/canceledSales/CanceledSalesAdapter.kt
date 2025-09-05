package br.com.mobicare.cielo.mySales.presentation.ui.adapter.canceledSales

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.CanceledSale

class CanceledSalesAdapter(private val clickListener: (canceledSale: CanceledSale) -> Unit):
    RecyclerView.Adapter<CanceledSaleViewHolder>() {

    private var canceledSales: MutableList<CanceledSale> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanceledSaleViewHolder {
        val item = LayoutItemMinhasVendasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CanceledSaleViewHolder(item,clickListener)
    }

    override fun getItemCount(): Int  = canceledSales.size

    override fun onBindViewHolder(holder: CanceledSaleViewHolder, position: Int) {
        holder.bind(canceledSales[position])
    }

    fun setCanceledSales(list: MutableList<CanceledSale>) {
        canceledSales.clear()
        canceledSales.addAll(list)
        notifyDataSetChanged()
    }

    fun addCanceledSales(list: List<CanceledSale>) {
        canceledSales.addAll(list)
        notifyDataSetChanged()
    }


}