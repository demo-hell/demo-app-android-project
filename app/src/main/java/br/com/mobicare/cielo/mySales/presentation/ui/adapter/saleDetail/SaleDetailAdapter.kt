package br.com.mobicare.cielo.mySales.presentation.ui.adapter.saleDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemExtratoDetalheBinding

class SaleDetailAdapter(
    private val listOfSaleDetailsItem: MutableList<Pair<String,String>>
): RecyclerView.Adapter<SaleDetailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleDetailViewHolder {
        val binding = ItemExtratoDetalheBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleDetailViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfSaleDetailsItem.size


    override fun onBindViewHolder(holder: SaleDetailViewHolder, position: Int) {
        holder.bind(listOfSaleDetailsItem[position])
    }

}