package br.com.mobicare.cielo.mySales.presentation.ui.adapter.consolidatedSales

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FORMAT_MINIMUM_2_DIGITS
import br.com.mobicare.cielo.commons.utils.getBrazilianDayAndExtensiveMonth
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.ItemExtratoListBinding
import br.com.mobicare.cielo.mySales.data.model.SaleHistory

class ConsolidatedSalesViewHolder (
    private val binding: ItemExtratoListBinding,
    private val clickListener: (String) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SaleHistory) {
        item.date?.let { itDate -> configureDate(itDate,binding) }
        item.quantity?.let { itQuantity -> configureQuantity(itQuantity,binding) }
        item.amount?.let { itAmount ->
            binding.textviewItemExtratoListValue.text = itAmount.toPtBrRealString()
        }
        binding.root.setOnClickListener {
            item.date?.let { itDate ->
                clickListener(itDate)
            }
        }
    }


    private fun configureDate(date: String, binding: ItemExtratoListBinding)  {
        val datePair = date.getBrazilianDayAndExtensiveMonth()
        binding.textviewItemExtratoListDate.text = itemView.context.getString(R.string.consolidate_sales_extensive_date,
            datePair.first.toString(), datePair.second)
    }

    private fun configureQuantity(quantity: Int, binding: ItemExtratoListBinding){
        val context = binding.root.context
        val resourceQuantityString = context.resources.getQuantityString(
            R.plurals.extrato_quantity,
            quantity,
            String.format(FORMAT_MINIMUM_2_DIGITS, quantity)
        )
        binding.textviewItemExtratoListQuantity.text = resourceQuantityString
    }
}