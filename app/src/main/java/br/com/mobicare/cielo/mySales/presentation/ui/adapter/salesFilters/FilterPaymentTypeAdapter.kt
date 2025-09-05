package br.com.mobicare.cielo.mySales.presentation.ui.adapter.salesFilters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemPaymentTypeSalesFilterBinding
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable


@SuppressLint("NotifyDataSetChanged")
class FilterPaymentTypeAdapter: RecyclerView.Adapter<FilterPaymentTypeViewHolder>()  {

    private var paymentTypesList: MutableList<ItemSelectable<PaymentType>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterPaymentTypeViewHolder {
        val binding = LayoutItemPaymentTypeSalesFilterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FilterPaymentTypeViewHolder(binding)
    }

    override fun getItemCount(): Int = paymentTypesList.size

    override fun onBindViewHolder(holder: FilterPaymentTypeViewHolder, position: Int) {
        holder.bind(paymentTypesList[position])
    }

    fun updateAdapter(paymentTypesList: List<ItemSelectable<PaymentType>>) {
        this.paymentTypesList.clear()
        this.paymentTypesList.addAll(paymentTypesList)
        notifyDataSetChanged()
    }

}