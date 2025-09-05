package br.com.mobicare.cielo.turboRegistration.presentation.businessSector

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.ONE_NEGATIVE
import br.com.mobicare.cielo.databinding.ItemLineBusinessBinding
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc

class BusinessSectorAdapter :
    RecyclerView.Adapter<BusinessSectorAdapter.BusinessSectorViewHolder>() {

    private val listBusiness = mutableListOf<Mcc>()
    private var action: ((Mcc) -> Unit)? = null
    private var selectedItemIndex: Int = ONE_NEGATIVE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessSectorViewHolder {
        val binding =
            ItemLineBusinessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BusinessSectorViewHolder(binding)
    }

    override fun getItemCount() = listBusiness.size

    override fun onBindViewHolder(holder: BusinessSectorViewHolder, position: Int) {
        holder.bind(listBusiness[position], position == selectedItemIndex)
    }

    fun setData(list: List<Mcc>) {
        listBusiness.clear()
        listBusiness.addAll(list)
        notifyDataSetChanged()
    }

    fun setAction(action: (Mcc) -> Unit) {
        this.action = action
    }

    fun setSelectedItem(index: Int) {
        selectedItemIndex = index
        notifyDataSetChanged()
    }

    inner class BusinessSectorViewHolder(private val binding: ItemLineBusinessBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(businessSector: Mcc, isSelected: Boolean) {
            binding.apply {
                tvLineBusiness.text = businessSector.description
                rbChoose.isSelected = isSelected
            }

            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position != selectedItemIndex) {
                    selectedItemIndex = position
                    notifyDataSetChanged()
                    action?.invoke(businessSector)
                }
            }
        }
    }
}