package br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasBinding
import br.com.mobicare.cielo.mySales.data.model.Sale

class TransactionSaleViewHolder(
    private val binding: LayoutItemMinhasVendasBinding,
    private val clickListener: (Sale) -> Unit): RecyclerView.ViewHolder(binding.root) {

        fun bind(sale: Sale){
            TransactionsSaleViewHolderUtils.bindSaleTransactionItem(
                sale = sale,
                clickListener = clickListener,
                binding = binding
            )
        }
}