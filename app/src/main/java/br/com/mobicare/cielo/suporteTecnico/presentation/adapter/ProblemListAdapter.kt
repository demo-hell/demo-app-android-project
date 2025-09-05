package br.com.mobicare.cielo.suporteTecnico.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemProblemListBinding
import br.com.mobicare.cielo.suporteTecnico.data.Problems

class ProblemListAdapter(
    private val problems: List<Problems>,
    private var onTap: ((Problems) -> Unit)? = null,
    private var onEmptySubProblem: ((Problems) -> Unit)? = null
) : RecyclerView.Adapter<ProblemListAdapter.ProblemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProblemViewHolder {
        val binding = ItemProblemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProblemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProblemViewHolder, position: Int) {
        onTap?.let { holder.bind(problems[position], it) }
    }

    override fun getItemCount() = problems.size

    inner class ProblemViewHolder(private val binding: ItemProblemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Problems, onTap: ((Problems) -> Unit)) {
            binding.apply {
                tvTitleItemView.text = item.name
            }
            itemView.setOnClickListener {
                if (item.subProblem.isEmpty()) {
                    onEmptySubProblem?.invoke(item)
                } else {
                    onTap?.invoke(item)
                }
            }
        }
    }
}