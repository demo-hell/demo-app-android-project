package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Term
import br.com.mobicare.cielo.databinding.LayoutTermLinkBinding

class TermLinkAdapter(
    private val items: List<Term>,
    private val listener: TermLinkContract.View
) : RecyclerView.Adapter<TermLinkAdapter.TermLinkViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TermLinkViewHolder {
        val binding = LayoutTermLinkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TermLinkViewHolder(binding, listener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TermLinkViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class TermLinkViewHolder(
        private val binding: LayoutTermLinkBinding,
        private val listener: TermLinkContract.View
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Term) {
            binding.apply {
                tvReadTermsLink.text = item.description

                root.setOnClickListener {
                    listener.onTermClick(item)
                }
            }
        }

    }

}