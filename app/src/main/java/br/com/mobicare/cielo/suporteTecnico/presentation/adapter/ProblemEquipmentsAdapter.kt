package br.com.mobicare.cielo.suporteTecnico.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemProblemListBinding
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments

class ProblemEquipmentsAdapter(
    private var onTap: (ProblemEquipments) -> Unit,
    private var onEmptySubProblems: () -> Unit
) : RecyclerView.Adapter<ProblemEquipmentsAdapter.ProblemViewHolder>() {

    private var items = emptyList<ProblemEquipments>()
    private var filteredItems = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemViewHolder {
        val binding = ItemProblemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProblemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProblemViewHolder, position: Int) {
        holder.bind(filteredItems[position])
    }

    override fun getItemCount() = filteredItems.size

    fun setItems(newItems: List<ProblemEquipments>) {
        items = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    inner class ProblemViewHolder(private val binding: ItemProblemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProblemEquipments) {
            binding.apply {
                tvTitleItemView.text = item.description
            }
            itemView.setOnClickListener {
                if (item.options.isNullOrEmpty()) {
                    onEmptySubProblems.invoke()
                } else {
                    onTap.invoke(item)
                }
            }
        }
    }
}