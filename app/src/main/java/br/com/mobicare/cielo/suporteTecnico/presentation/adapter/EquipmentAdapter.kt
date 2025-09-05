package br.com.mobicare.cielo.suporteTecnico.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemMerchantMachineBinding
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.suporteTecnico.utils.EnumMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine

class EquipmentAdapter(
    private var onTap: ((TaxaPlanosMachine) -> Unit)? = null
) : RecyclerView.Adapter<EquipmentAdapter.EquipmentViewHolder>() {

    private var items = emptyList<TaxaPlanosMachine>()
    private var filteredItems = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val binding = ItemMerchantMachineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount() = filteredItems.size

    fun setItems(newItems: List<TaxaPlanosMachine>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun filter(input: String) {
        filteredItems = if (input.isNotBlank()) getFilteredItems(input) else items
        notifyDataSetChanged()
    }

    private fun getFilteredItems(text: String) = items.filter {
        it.logicalNumber.toString() == text
                || it.logicalNumber.onlyDigits().contains(text.onlyDigits(), ignoreCase = true)
    }

    inner class EquipmentViewHolder(private val binding: ItemMerchantMachineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaxaPlanosMachine) {
            binding.apply {
                tvTitleItemView.text = item.model
                tvLogicalNumber.text =
                    context.getString(R.string.machine_number, item.logicalNumber)

                val machineImage = EnumMachine.findImageByItemName(item.commercialDescription)
                ivMerchantMachine.setImageResource(machineImage)
            }
            itemView.setOnClickListener {
                onTap?.invoke(item)
            }
        }
    }
}