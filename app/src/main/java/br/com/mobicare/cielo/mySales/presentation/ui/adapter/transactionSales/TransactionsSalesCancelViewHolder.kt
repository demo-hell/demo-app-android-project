package br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasManBinding
import br.com.mobicare.cielo.mySales.data.model.Sale

class TransactionsSalesCancelViewHolder(
    private val binding: LayoutItemMinhasVendasManBinding,
    private val clickListener: (Sale) -> Unit,
    private val cancelClickListener: (Sale) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(sale: Sale){
        TransactionsSaleViewHolderUtils.bindSaleTransactionItem(
            sale = sale,
            clickListener = clickListener,
            binding = binding.layoutItemMinhasVendas
        )

        binding.layoutOptionsDelete.llCancel.setOnClickListener {
            cancelClickListener.invoke(sale)
        }
    }
}