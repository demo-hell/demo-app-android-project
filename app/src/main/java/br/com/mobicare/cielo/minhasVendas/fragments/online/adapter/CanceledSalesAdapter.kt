package br.com.mobicare.cielo.minhasVendas.fragments.online.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.minhasVendas.fragments.online.MinhasVendasOnlineContract
import br.com.mobicare.cielo.minhasVendas.fragments.online.viewHolder.CanceledSaleViewHolder

class CanceledSalesAdapter(private val listener: MinhasVendasOnlineContract.WithCanceledSellsView) :
    RecyclerView.Adapter<CanceledSaleViewHolder>() {

    private var canceledSales: ArrayList<CanceledSale> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CanceledSaleViewHolder {
        val item = LayoutItemMinhasVendasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CanceledSaleViewHolder(item, listener)
    }

    override fun onBindViewHolder(holder: CanceledSaleViewHolder, position: Int) {
        holder.bind(canceledSales[position])
    }

    override fun getItemCount(): Int {
        return canceledSales.count()
    }

    fun setCanceledSales(list: List<CanceledSale>) {
        canceledSales.clear()
        canceledSales.addAll(list)
    }

    fun addCanceledSales(list: List<CanceledSale>) {
        canceledSales.addAll(list)
        notifyDataSetChanged()
    }
}